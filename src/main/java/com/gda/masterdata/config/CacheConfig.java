package com.gda.masterdata.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // Spring Boot 3.x автоматически использует Jackson для сериализации значений
        // Нам нужно настроить только TTL и префиксы

        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(7))
            .disableCachingNullValues()
            .computePrefixWith(cacheName -> CacheConstants.APP_PREFIX + ":" + cacheName + "::")
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // Значения автоматически сериализуются в JSON с помощью Jackson
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}