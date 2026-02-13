package com.example.rbac.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.rbac.cache.CaffeineCacheServiceImpl;
import com.example.rbac.cache.RedisCacheServiceImpl;
import com.example.rbac.enums.CacheTypeEnum;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * Caffeine缓存配置：
 * 当没有Redis缓存实现（RedisCacheServiceImpl）时生效
 */
@Configuration
public class CaffeineCacheAutoConfig {

    /**
     * 初始化Caffeine缓存实例
     */
    @Bean
    @ConditionalOnMissingBean(RedisCacheServiceImpl.class)
    public com.github.benmanes.caffeine.cache.Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .maximumSize(10000) // 最大缓存数
                .expireAfterWrite(30, TimeUnit.MINUTES) // 写入后30分钟过期
                .recordStats() // 统计命中率
                .build();
    }

    /**
     * 注册Caffeine缓存实现（@Primary确保兜底）
     */
    @Bean
    @Primary // 优先使用（当Redis缓存不存在时）
    @ConditionalOnMissingBean(RedisCacheServiceImpl.class)
    public CaffeineCacheServiceImpl caffeineCacheService(com.github.benmanes.caffeine.cache.Cache<String, Object> caffeineCache) {
        return new CaffeineCacheServiceImpl(caffeineCache);
    }

    /**
     * 注册缓存类型（标识当前用Caffeine）
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "cacheType")
    public CacheTypeEnum cacheTypeCaffeine() {
        return CacheTypeEnum.CAFFEINE;
    }

}
