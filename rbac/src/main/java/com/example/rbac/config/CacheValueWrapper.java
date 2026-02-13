package com.example.rbac.config;

public class CacheValueWrapper {

    private final Object value;
    private final long expireSeconds;

    public CacheValueWrapper(Object value, long expireSeconds) {
        this.value = value;
        this.expireSeconds = expireSeconds;
    }

    public Object getValue() {
        return value;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }
}
