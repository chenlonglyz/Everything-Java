package com.example.rbac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(RedisTemplate.class)
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHealthIndicator(@Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Health health() {
        try {
            redisTemplate.execute((RedisCallback<String>) RedisConnection::ping);
            return Health.up().withDetail("redis", "available").build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("redis", "unavailable")
                    .build();
        }
    }

    /**
     * 判断Redis是否可用
     */
    public boolean isRedisAvailable() {
        return Status.UP.equals(health().getStatus());
    }
}
