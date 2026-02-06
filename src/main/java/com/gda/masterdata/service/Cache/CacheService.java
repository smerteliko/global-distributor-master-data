package com.gda.masterdata.service.Cache;

import com.gda.masterdata.config.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    // Spring Boot сам подтянет настроенный RedisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Очищает кэш по имени тега (например, "portal:modules").
     * Удалит все ключи, начинающиеся с этого префикса.
     */
    public long clearCacheByTag(String tagName) {
        // Добавляем wildcard в конце: "portal:modules*"
        // Это захватит "portal:modules::all", "portal:modules::id:1" и т.д.
        String pattern = CacheConstants.APP_PREFIX + ":" + tagName + "*";
        return deleteKeysByPattern(pattern);
    }

    /**
     * Полный сброс кэша (только ключи, созданные этим приложением).
     * Безопаснее, чем flushDb, так как не трогает чужие данные в том же Redis.
     */
    public long flushAll() {
        // Удаляем всё, что попадает под шаблон (можно уточнить префикс, если нужно)
        String pattern = CacheConstants.APP_PREFIX + ":*";
        return deleteKeysByPattern(pattern);
    }

    private long deleteKeysByPattern(String pattern) {
        Set<String> keys = new HashSet<>();

        // Используем SCAN (курсор), чтобы безопасно искать ключи
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build())) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            }
            return null;
        });

        if (keys.isEmpty()) {
            log.info("No keys found for pattern: {}", pattern);
            return 0;
        }

        // Удаляем найденные ключи
        Long count = redisTemplate.delete(keys);
        log.info("Cache flush: pattern='{}', deleted {} keys", pattern, count);
        return count != null ? count : 0;
    }
}
