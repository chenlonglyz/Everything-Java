package com.example.paymentgateway;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotentService {

    private final RedisTemplate<String, Object> redisTemplate;

    public IdempotentService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String key, long timeoutSeconds) {
        // 尝试获取锁 （设置锁的过期时间） 使用了命令 setIfAbsent 对应redis的setNX
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", timeoutSeconds, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(success);
    }
}
