package com.example.rbac.enums;

/**
 * 资源状态枚举
 */
public enum ResourceStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    public Integer getCode() {
        return code;
    }

    ResourceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static ResourceStatusEnum getByCode(Integer code) {
        for (ResourceStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
