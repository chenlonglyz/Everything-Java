package com.example.rbac.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.rbac.cache.CacheDegradeProxy;
import com.example.rbac.cache.CaffeineCacheServiceImpl;
import com.example.rbac.cache.DegradeCache;
import com.example.rbac.cache.RedisCacheServiceImpl;
import org.checkerframework.checker.nullness.qual.NonNull;

@Configuration
@EnableCaching // 必须开启，否则Spring Cache注解无效
public class DynamicCacheConfig {

    // ========== 1. 基础Bean配置 ==========
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 序列化配置
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheDegradeProxy cacheDegradeProxy(RedisHealthIndicator redisHealthIndicator,
                                               CaffeineCacheServiceImpl caffeineCacheService,
                                               RedisCacheServiceImpl redisCacheService) {
        CacheDegradeProxy proxy = new CacheDegradeProxy(caffeineCacheService);
        proxy.setRedisCacheService(redisCacheService);
        proxy.setRedisHealthIndicator(redisHealthIndicator);
        return proxy;
    }

    // ========== 2. 动态CacheManager（核心改造） ==========
    @Bean
    public CacheManager dynamicCacheManager(CacheDegradeProxy cacheDegradeProxy) {
        // 缓存名称 -> 过期时间（秒）映射
        Map<String, Long> cacheExpireMap = new HashMap<>();
        cacheExpireMap.put("rbac:perm", 3600L); // 1小时
        cacheExpireMap.put("rbac:resource", 1800L); // 30分钟

        return new CacheManager() {
            @Override
            public Cache getCache(@NonNull String name) {
                // 为每个缓存名称创建自定义DegradeCache实例
                Long expireSeconds = cacheExpireMap.getOrDefault(name, 3600L);
                return new DegradeCache(name, expireSeconds, cacheDegradeProxy);
            }

            @Override
            public @NonNull Collection<String> getCacheNames() {
                return cacheExpireMap.keySet();
            }
        };
    }
}
