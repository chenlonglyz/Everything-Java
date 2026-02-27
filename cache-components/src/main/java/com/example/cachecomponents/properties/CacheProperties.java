package com.example.cachecomponents.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "cache")
@Configuration
public class CacheProperties {

    /**
     * 是否启用本地缓存
     */
    private boolean enableCaffeine = true;

    /**
     * 是否启用 Redis
     */
    private boolean enableRedis = true;

    /**
     * Redis 启用时是否同步本地缓存
     */
    private boolean caffeineSyncRedis = true;

    /**
     * 默认过期时间（秒）
     */
    private long defaultExpire = 3600;

    public boolean isEnableCaffeine() { return enableCaffeine; }
    public void setEnableCaffeine(boolean enableCaffeine) { this.enableCaffeine = enableCaffeine; }

    public boolean isEnableRedis() { return enableRedis; }
    public void setEnableRedis(boolean enableRedis) { this.enableRedis = enableRedis; }

    public boolean isCaffeineSyncRedis() { return caffeineSyncRedis; }
    public void setCaffeineSyncRedis(boolean caffeineSyncRedis) { this.caffeineSyncRedis = caffeineSyncRedis; }

    public long getDefaultExpire() { return defaultExpire; }
    public void setDefaultExpire(long defaultExpire) { this.defaultExpire = defaultExpire; }
}

