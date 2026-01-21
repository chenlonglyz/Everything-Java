package com.example.rbac.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Status;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RedisTemplate.class)
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisHealthIndicator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Health health() {
        try {
            redisTemplate.execute((RedisCallback<String>) RedisConnection::ping);
            return Health.up().build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

    /**
     * 判断Redis是否可用
     */
    public boolean isRedisAvailable() {
        Health health = health();
        return health != null && health.getStatus().getCode().equals(Status.UP.getCode());
    }
}

