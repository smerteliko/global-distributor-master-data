package com.gda.masterdata.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
    /**
     * Создаем кастомный ObjectMapper.
     * Это нужно, чтобы:
     * 1. Нормально работали Java 8 даты (LocalDate).
     * 2. В JSON записывался класс объекта (@class), иначе достанем LinkedHashMap вместо DTO.
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // Используем GenericJackson2JsonRedisSerializer с нашим маппером
        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(createObjectMapper());

        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(7))
            .disableCachingNullValues()
            // Префикс для ключей (masterdata:portal:modules::...)
            .computePrefixWith(cacheName -> CacheConstants.APP_PREFIX + ":" + cacheName + "::")
            // Ключи - строки
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            // Значения - JSON (Generic serializer)
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    /**
     * Явный CacheManager, чтобы Spring точно использовал наш конфиг
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfiguration())
            .build();
    }

    /**
     * RedisTemplate для админских утилит (сброс кэша).
     * Настройки сериализации должны совпадать с кэшем!
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(createObjectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}