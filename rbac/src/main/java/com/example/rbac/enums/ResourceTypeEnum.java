package com.example.rbac.enums;

/**
 * 资源类型枚举
 */
public enum ResourceTypeEnum {
    API("API", "接口资源"),
    PAGE("PAGE", "页面资源"),
    BUTTON("BUTTON", "按钮资源");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    ResourceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static ResourceTypeEnum getByCode(String code) {
        for (ResourceTypeEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
