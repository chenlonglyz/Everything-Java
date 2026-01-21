package com.example.rbac.enums;

/**
 * 缓存类型枚举
 */
public enum CacheTypeEnum {
    REDIS, // Redis缓存（分布式）
    CAFFEINE // 本地Caffeine缓存
}
