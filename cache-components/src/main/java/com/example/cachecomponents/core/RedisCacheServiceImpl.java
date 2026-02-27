package com.example.cachecomponents.core;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Redis缓存实现
 *
 * <p>
 * 特点：
 * - 分布式
 * - 支持持久化
 * - 多节点共享
 *
 * 适合：
 * - 多实例部署
 * - 需要高一致性的缓存场景
 */
public class RedisCacheServiceImpl implements CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value, long expireSeconds) {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        valueOps.set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String key) {
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        Object value = valueOps.get(key);
        return value != null ? (T) value : null;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public CacheTypeEnum getCacheType() {
        return CacheTypeEnum.REDIS;
    }
}
