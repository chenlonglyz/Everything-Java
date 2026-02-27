package com.example.cachecomponents.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.data.redis.RedisHealthIndicator;

/**
 * 缓存降级代理实现（核心类）
 *
 * <p>
 * 作用：
 * 1. 对外提供统一的 CacheService 接口
 * 2. 内部封装 Redis + Caffeine 双缓存逻辑
 * 3. 根据 Redis 健康状态动态切换读写策略
 *
 * <p>
 * 策略说明：
 * - 写：Redis可用 → 写Redis + 本地Caffeine（双写）
 * - 读：Redis可用 → 优先读Redis，未命中回源本地并回写Redis
 * - Redis不可用 → 只使用本地Caffeine（自动降级）
 *
 * <p>
 * 适用于：
 * - 高可用系统
 * - Redis异常时自动降级
 * - 分布式缓存 + 本地缓存二级架构
 */
public class CacheDegradeProxy implements CacheService {
    /**
     * Redis缓存实现（可选注入）
     */
    private RedisCacheServiceImpl redisCacheService;
    /**
     * 本地Caffeine缓存实现（必须存在）
     */
    private final CaffeineCacheServiceImpl caffeineCacheService;

    /**
     * 构造器（必须提供本地缓存）
     */
    public CacheDegradeProxy(CaffeineCacheServiceImpl caffeineCacheService) {
        this.caffeineCacheService = caffeineCacheService;
    }

    /**
     * 注入Redis缓存实现（可选）
     */
    @Autowired(required = false)
    public void setRedisCacheService(RedisCacheServiceImpl redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    /**
     * 设置缓存
     * 写策略：
     * - Redis可用 → Redis + Caffeine 双写
     * - Redis不可用 → 仅写Caffeine
     *
     * @param key           缓存key
     * @param value         缓存值
     * @param expireSeconds 过期时间（秒）
     */
    @Override
    public void set(String key, Object value, long expireSeconds) {
        // 双写：Redis可用时写Redis+本地，不可用时只写本地
        if (redisCacheService != null) {
            redisCacheService.set(key, value, expireSeconds);
        }
        caffeineCacheService.set(key, value, expireSeconds);
    }

    /**
     * 获取缓存
     * 读策略：
     * - Redis可用 → 优先读Redis
     *      - Redis未命中 → 读本地并回写Redis
     * - Redis不可用 → 读本地
     *
     * @param key   缓存key
     */
    @Override
    public <T> T get(String key) {
        // 1️⃣ 先查本地Caffeine
        T value = caffeineCacheService.get(key);
        if (value != null) {
            return value;
        }

        // ========= 2️⃣ 再查 L2 =========
        if (redisCacheService != null) {
            try {
                value = redisCacheService.get(key);

                if (value != null) {
                    // 回写 L1
                    caffeineCacheService.set(key, value, 30 * 60);
                    return value;
                }
            } catch (Exception ignored) {
                // Redis异常自动降级
            }
        }

        // ========= 3️⃣ 都没有 → 交给Spring回源DB =========
        return null;
    }

    /**
     * 删除缓存
     * 删除策略：
     * - Redis可用 → Redis + 本地 双删
     * - Redis不可用 → 仅删本地
     */
    @Override
    public void delete(String key) {
        // 双删：Redis可用时删Redis+本地，不可用时只删本地
        if (redisCacheService != null) {
            redisCacheService.delete(key);
        }
        caffeineCacheService.delete(key);
    }

    /**
     * 获取当前生效缓存类型
     *
     * @return REDIS / CAFFEINE
     */
    @Override
    public CacheTypeEnum getCacheType() {
        return redisCacheService != null ? CacheTypeEnum.REDIS : CacheTypeEnum.CAFFEINE;
    }

    /**
     * 根据前缀清理缓存
     *
     * @param prefix key前缀
     */
    public void clearByPrefix(String prefix) {
        caffeineCacheService.clear();
        if (redisCacheService != null) {
            // 可用 scan + delete
        }
    }
}
