package com.example.rbac.cache;

import com.example.rbac.enums.CacheTypeEnum;

/**
 * 统一缓存接口：所有缓存操作通过该接口，底层实现由配置决定
 */
public interface CacheService {
    /**
     * 设置缓存
     */
    void set(String key, Object value, long expireSeconds);

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 获取缓存类型
     */
    CacheTypeEnum getCacheType();
}
