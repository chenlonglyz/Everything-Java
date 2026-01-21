package com.example.rbac.cache;

import org.springframework.stereotype.Component;

import com.example.rbac.enums.CacheTypeEnum;
import com.github.benmanes.caffeine.cache.Cache;

/**
 * Caffeine缓存实现
 */
@Component
public class CaffeineCacheServiceImpl implements CacheService {
    private final Cache<String, Object> caffeineCache;

    public CaffeineCacheServiceImpl(Cache<String, Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void set(String key, Object value, long expireSeconds) {
        // Caffeine的过期时间在初始化时已配置，这里忽略expireSeconds（也可动态设置）
        caffeineCache.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = caffeineCache.getIfPresent(key);
        return value != null ? (T) value : null;
    }

    @Override
    public void delete(String key) {
        caffeineCache.invalidate(key);
    }

    @Override
    public CacheTypeEnum getCacheType() {
        return CacheTypeEnum.CAFFEINE;
    }
}
