package com.example.rbac.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    @ConditionalOnBean(RedisTemplate.class)
    public RedisCacheServiceImpl redisCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheServiceImpl(redisTemplate);
    }

    @Bean
    public CacheDegradeProxy cacheDegradeProxy(@Autowired(required = false) RedisHealthIndicator redisHealthIndicator,
                                               CaffeineCacheServiceImpl caffeineCacheService,
                                               @Autowired(required = false)RedisCacheServiceImpl redisCacheService) {
        CacheDegradeProxy proxy = new CacheDegradeProxy(caffeineCacheService);
        proxy.setRedisCacheService(redisCacheService);
        proxy.setRedisHealthIndicator(redisHealthIndicator);
        return proxy;
    }

    // ========== 2. 动态CacheManager（核心改造） ==========
    @Bean
    @Primary
    public CacheManager dynamicCacheManager(CacheDegradeProxy proxy) {

        Map<String, Long> cacheExpireMap = Map.of(
                "rbac:perm", 3600L,
                "rbac:resource", 1800L
        );

        ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

        return new CacheManager() {

            @Override
            public Cache getCache(String name) {
                return cacheMap.computeIfAbsent(name, n -> {
                    Long expire = cacheExpireMap.getOrDefault(n, 3600L);
                    return new DegradeCache(n, expire, proxy);
                });
            }

            @Override
            public Collection<String> getCacheNames() {
                return cacheExpireMap.keySet();
            }
        };
    }
}
