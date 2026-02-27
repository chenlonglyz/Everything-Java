package com.example.cachecomponents.core;

import com.github.benmanes.caffeine.cache.Cache;

/**
 * Caffeine本地缓存实现
 *
 * <p>
 * 特点：
 * - 基于JVM内存
 * - 访问速度快
 * - 适合热点数据
 *
 * 注意：
 * - 不支持分布式
 * - 服务重启会丢失数据
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
    public <T> T get(String key) {
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
