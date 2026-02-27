package com.example.cachecomponents.core;

/**
 * 缓存值包装类
 *
 * <p>
 * 作用：
 * - 包装真实缓存值
 * - 记录该值的过期时间
 * - 用于Caffeine动态过期策略
 */
public class CacheValueWrapper {

    /**
     * 实际缓存值
     */
    private final Object value;
    /**
     * 过期时间（秒）
     */
    private final long expireSeconds;

    public CacheValueWrapper(Object value, long expireSeconds) {
        this.value = value;
        this.expireSeconds = expireSeconds;
    }

    public Object getValue() {
        return value;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }
}
