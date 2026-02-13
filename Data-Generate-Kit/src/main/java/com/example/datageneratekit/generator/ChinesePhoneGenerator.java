package com.example.datageneratekit.generator;

import java.util.Random;

import com.example.datageneratekit.core.DataGenerator;
import com.example.datageneratekit.enums.CountryType;

public class ChinesePhoneGenerator implements DataGenerator<String> {
    // 中国手机号号段
    private static final String[] PHONE_PREFIX = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "150", "151", "152", "153", "155", "156", "157", "158", "159", "170", "171", "173", "175", "176", "177", "178", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
    private final Random random = new Random();

    @Override
    public String generate(CountryType countryType) {
        if (CountryType.CHINA != countryType) {
            throw new UnsupportedOperationException("暂不支持该国家的手机号生成");
        }
        String prefix = PHONE_PREFIX[random.nextInt(PHONE_PREFIX.length)];
        StringBuilder suffix = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            suffix.append(random.nextInt(10));
        }
        return prefix + suffix;
    }
}