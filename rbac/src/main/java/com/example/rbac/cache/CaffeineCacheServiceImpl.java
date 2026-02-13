package com.example.rbac.cache;

import org.springframework.stereotype.Component;

import com.example.rbac.config.CacheValueWrapper;
import com.example.rbac.enums.CacheTypeEnum;
import com.github.benmanes.caffeine.cache.Cache;

/**
 * Caffeine缓存实现
 */
public class CaffeineCacheServiceImpl implements CacheService {

    private final Cache<String, Object> caffeineCache;

    public CaffeineCacheServiceImpl(Cache<String, Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }

    @Override
    public void set(String key, Object value, long expireSeconds) {
        caffeineCache.put(key, new CacheValueWrapper(value, expireSeconds));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object wrapper = caffeineCache.getIfPresent(key);
        if (wrapper instanceof CacheValueWrapper valueWrapper) {
            return (T) valueWrapper.getValue();
        }
        return null;
    }

    @Override
    public void delete(String key) {
        caffeineCache.invalidate(key);
    }

    @Override
    public CacheTypeEnum getCacheType() {
        return CacheTypeEnum.CAFFEINE;
    }

    public void clear() {
        caffeineCache.invalidateAll();
    }
}
