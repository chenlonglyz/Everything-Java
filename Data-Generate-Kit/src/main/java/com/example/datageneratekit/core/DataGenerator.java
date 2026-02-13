package com.example.datageneratekit.core;

import com.example.datageneratekit.enums.CountryType;

public interface DataGenerator<T> {
    /**
     * 根据国家类型生成数据
     */
    T generate(CountryType countryType);

    /**
     * 默认生成中国数据
     */
    default T generate() {
        return generate(CountryType.CHINA);
    }
}
