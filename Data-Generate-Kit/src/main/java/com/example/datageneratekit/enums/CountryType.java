package com.example.datageneratekit.enums;

public enum CountryType {
    CHINA("CN", "中国"),
    // 后续可扩展其他国家
    ;

    private final String code;
    private final String name;

    CountryType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
