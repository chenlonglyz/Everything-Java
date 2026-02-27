package com.example.cachecomponents.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.Nullable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.NonNull;

/**
 * 自定义Spring Cache实现
 *
 * <p>
 * 作用：
 * - 适配Spring Cache注解体系（@Cacheable）
 * - 内部使用CacheDegradeProxy实现双缓存逻辑
 *
 * 设计目标：
 * - 兼容Spring原生缓存注解
 * - 支持动态过期时间
 * - 支持随机过期（防止缓存雪崩）
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
    public ValueWrapper get(@NonNull Object key) {
        String fullKey = buildFullKey(key);
        Object value = cacheDegradeProxy.get(fullKey);
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    @Nullable
    public <T> T get(@NonNull Object key, @Nullable Class<T> type) {
        String fullKey = buildFullKey(key);
        return cacheDegradeProxy.get(fullKey);
    }

    @Override
    @Nullable
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        String fullKey = buildFullKey(key);
        T value = cacheDegradeProxy.get(fullKey);
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
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        if (value == null) {
            return get(key);
        }
        String fullKey = buildFullKey(key);
        Object oldValue = cacheDegradeProxy.get(fullKey);
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
