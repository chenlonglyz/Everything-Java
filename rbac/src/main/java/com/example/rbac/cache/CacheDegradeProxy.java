package com.example.rbac.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.rbac.config.RedisHealthIndicator;
import com.example.rbac.enums.CacheTypeEnum;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class CacheDegradeProxy implements CacheService {
    private RedisCacheServiceImpl redisCacheService;
    private final CaffeineCacheServiceImpl caffeineCacheService;
    private RedisHealthIndicator redisHealthIndicator;

    public CacheDegradeProxy(CaffeineCacheServiceImpl caffeineCacheService) {
        this.caffeineCacheService = caffeineCacheService;
    }

    @Autowired(required = false)
    public void setRedisCacheService(RedisCacheServiceImpl redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @Autowired(required = false)
    public void setRedisHealthIndicator(RedisHealthIndicator redisHealthIndicator) {
        this.redisHealthIndicator = redisHealthIndicator;
    }

    @Override
    public void set(String key, Object value, long expireSeconds) {
        // 双写：Redis可用时写Redis+本地，不可用时只写本地
        if (redisHealthIndicator !=null && redisHealthIndicator.isRedisAvailable()) {
            redisCacheService.set(key, value, expireSeconds);
        }
        caffeineCacheService.set(key, value, expireSeconds);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        // 读：Redis可用时读Redis，不可用时读本地
        if (redisHealthIndicator !=null && redisHealthIndicator.isRedisAvailable()) {
            T value = redisCacheService.get(key, clazz);
            // Redis未命中时，读本地并回写Redis
            if (value == null) {
                value = caffeineCacheService.get(key, clazz);
                if (value != null) {
                    redisCacheService.set(key, value, 30 * 60);
                }
            }
            return value;
        } else {
            return caffeineCacheService.get(key, clazz);
        }
    }

    @Override
    public void delete(String key) {
        // 双删：Redis可用时删Redis+本地，不可用时只删本地
        if (redisHealthIndicator !=null && redisHealthIndicator.isRedisAvailable()) {
            redisCacheService.delete(key);
        }
        caffeineCacheService.delete(key);
    }

    @Override
    public CacheTypeEnum getCacheType() {
        return redisHealthIndicator !=null && redisHealthIndicator.isRedisAvailable() ? CacheTypeEnum.REDIS : CacheTypeEnum.CAFFEINE;
    }
}
