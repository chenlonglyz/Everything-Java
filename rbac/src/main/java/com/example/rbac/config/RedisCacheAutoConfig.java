package com.example.rbac.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.rbac.cache.RedisCacheServiceImpl;
import com.example.rbac.enums.CacheTypeEnum;

/**
 * Redis缓存配置：
 * 1. 有Redis依赖（RedisTemplate）
 * 2. 有Redis配置（spring.redis.host存在）
 * 满足以上两个条件时生效
 */
@Configuration
// 有RedisTemplate类（即引入了Redis依赖）
@ConditionalOnClass(RedisTemplate.class)
@ConditionalOnBean(RedisTemplate.class)
// 有Redis配置（spring.redis.host不为空）
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
public class RedisCacheAutoConfig {

    /**
     * 注册Redis缓存实现
     */
    @Bean
    public RedisCacheServiceImpl redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheServiceImpl(redisTemplate);
    }

    /**
     * 注册缓存类型（标识当前用Redis）
     */
    @Bean
    public CacheTypeEnum cacheType() {
        return CacheTypeEnum.REDIS;
    }
}
