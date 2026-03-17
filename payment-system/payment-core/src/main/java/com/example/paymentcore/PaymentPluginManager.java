package com.example.paymentcore;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import com.example.paymentapi.PaymentMethod;
import com.example.paymentapi.PaymentPlugin;

/**
 * 支付插件管理器
 */
public class PaymentPluginManager {

    /**
     * 支付插件缓存
     */
    private final Map<String, PaymentPlugin> pluginMap = new HashMap<>();

    public PaymentPluginManager() {
        ServiceLoader<PaymentPlugin> loader = ServiceLoader.load(PaymentPlugin.class);
        for (PaymentPlugin plugin : loader) {

            if (pluginMap.containsKey(plugin.getPayCode())) {

                throw new RuntimeException("Duplicate payment plugin: " + plugin.getPayCode());
            }
            PaymentMethod paymentMethod = plugin.getPaymentMethod();
            if (Boolean.TRUE.equals(paymentMethod.getEnable())) {
                pluginMap.put(plugin.getPayCode(), plugin);
            }
        }
    }

    /**
     * 获取插件
     */
    public PaymentPlugin getPlugin(String code) {

        PaymentPlugin plugin = pluginMap.get(code);

        if (plugin == null) {
            throw new RuntimeException("Unsupported payment: " + code);
        }

        return plugin;
    }

    /**
     * 获取所有插件
     */
    public Map<String, PaymentPlugin> getPlugins() {

        return pluginMap;
    }

}
