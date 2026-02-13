package com.example.rbac.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class RedisHealthIndicator implements HealthIndicator {
    private final RedisTemplate<String, Object> redisTemplate;

    private final AtomicBoolean redisAvailable = new AtomicBoolean(true);

    public RedisHealthIndicator(@Autowired(required = false)
                                RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 每5秒检测一次Redis
     */
    @Scheduled(fixedDelay = 5000)
    public void doPing() {
        if (redisTemplate == null) {
            redisAvailable.set(false);
            return;
        }
        try {
            redisTemplate.execute((RedisCallback<String>) RedisConnection::ping);
            redisAvailable.set(true);
        } catch (Exception e) {
            redisAvailable.set(false);
        }
    }

    public boolean isRedisAvailable() {
        return redisAvailable.get();
    }

    @Override
    public Health health() {
        return redisAvailable.get()
                ? Health.up().withDetail("redis", "available").build()
                : Health.down().withDetail("redis", "unavailable").build();
    }
}
