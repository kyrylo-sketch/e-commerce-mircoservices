package com.wex.product_service.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wex.product_service.model.Product;
import io.lettuce.core.RedisChannelHandler;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
//       var serializer = GenericJacksonJsonRedisSerializer.builder()
//               .typePropertyName("@class")
//               .build();
        var serializer = new JacksonJsonRedisSerializer<>(Product.class);

       RedisTemplate<String, Object> template = new RedisTemplate<>();
       template.setConnectionFactory(factory);
       template.setKeySerializer(new StringRedisSerializer());
       template.setValueSerializer(serializer);
       template.setHashKeySerializer(new StringRedisSerializer());
       template.setHashValueSerializer(serializer);
       return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        System.out.println("MY CACHE MANAGER");
        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .typePropertyName("@class")
                .build();
//        var serializer = new JacksonJsonRedisSerializer<>(Product.class);

        var cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new  StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(cacheConfig).build();
    }

}