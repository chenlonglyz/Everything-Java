package com.example.cachecomponents.core;

/**
 * 缓存类型枚举
 *
 * REDIS     → 分布式缓存
 * CAFFEINE  → 本地内存缓存
 */
public enum CacheTypeEnum {
    REDIS, // Redis缓存（分布式）
    CAFFEINE // 本地Caffeine缓存
}
