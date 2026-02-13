package com.example.rbac.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.Nullable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * 自定义Cache实现，封装CacheDegradeProxy的双缓存逻辑
 * 适配Spring Cache接口，让注解能调用自定义的缓存代理
 */
public class DegradeCache implements Cache {
    // 缓存名称（对应@Cacheable的value）
    private final String name;
    // 缓存过期时间（秒）
    private final long expireSeconds;
    // 自定义缓存代理
    private final CacheDegradeProxy cacheDegradeProxy;

    public DegradeCache(String name, long expireSeconds, CacheDegradeProxy cacheDegradeProxy) {
        this.name = name;
        this.expireSeconds = expireSeconds;
        this.cacheDegradeProxy = cacheDegradeProxy;
    }

    // Spring Cache的缓存Key格式：缓存名称::具体Key（如rbac:perm::123）
    private String buildFullKey(Object key) {
        return this.name + "::" + key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        // 返回底层缓存代理（用于调试/监控）
        return this.cacheDegradeProxy;
    }

    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        String fullKey = buildFullKey(key);
        Object value = cacheDegradeProxy.get(fullKey, Object.class);
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        String fullKey = buildFullKey(key);
        return cacheDegradeProxy.get(fullKey, type);
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        String fullKey = buildFullKey(key);
        T value = cacheDegradeProxy.get(fullKey, (Class<T>) Object.class);
        if (value != null) {
            return value;
        }
        // 未命中时调用valueLoader（如@Cacheable的方法逻辑），并写入缓存
        try {
            value = valueLoader.call();
            if (value != null) {
                cacheDegradeProxy.set(fullKey, value , randomExpire(this.expireSeconds));
            }
            return value;
        }
        catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        if (value == null) {
            return; // 禁止缓存null值
        }
        String fullKey = buildFullKey(key);
        cacheDegradeProxy.set(fullKey, value, this.expireSeconds);
    }

    @Override
    @Nullable
    public Cache.ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        if (value == null) {
            return get(key);
        }
        String fullKey = buildFullKey(key);
        Object oldValue = cacheDegradeProxy.get(fullKey, Object.class);
        if (oldValue == null) {
            cacheDegradeProxy.set(fullKey, value, this.expireSeconds);
            return null;
        }
        else {
            return new SimpleValueWrapper(oldValue);
        }
    }

    @Override
    public void evict(Object key) {
        String fullKey = buildFullKey(key);
        cacheDegradeProxy.delete(fullKey);
    }

    @Override
    public void clear() {
        cacheDegradeProxy.clearByPrefix(this.name + "::");
    }

    private long randomExpire(long baseExpire) {
        return baseExpire + ThreadLocalRandom.current().nextLong(60);
    }
}
