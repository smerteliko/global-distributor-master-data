package com.gda.masterdata.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Настройка сериализации в JSON.
     * Чтобы в Redis данные лежали в читаемом JSON формате, а не в бинарном Java-формате.
     */
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        ObjectMapper objectMapper = new ObjectMapper();

        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(objectMapper);
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(7))
            .disableCachingNullValues()
            .computePrefixWith(cacheName -> CacheConstants.APP_PREFIX + ":" + cacheName + "::")
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
