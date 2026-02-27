package com.example.cachecomponents.core;


/**
 * 统一缓存接口
 *
 * <p>
 * 所有缓存操作必须通过该接口进行，
 * 底层实现可以是：
 * - Redis
 * - Caffeine
 * - Redis + Caffeine 组合
 *
 * 设计目的：
 * - 解耦缓存实现
 * - 便于扩展
 * - 便于降级
 */
public interface CacheService {
    /**
     * 设置缓存
     */
    void set(String key, Object value, long expireSeconds);

    /**
     * 获取缓存
     */
    <T> T get(String key);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 获取缓存类型
     */
    CacheTypeEnum getCacheType();
}
