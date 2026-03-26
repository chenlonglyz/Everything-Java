package com.example.paymentcore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.example.paymentapi.PaymentMethod;
import com.example.paymentapi.PaymentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付插件管理器
 */
public class PaymentPluginManager {

    private final Map<String, PaymentPlugin> pluginMap = new HashMap<>();

    public PaymentPluginManager(List<PaymentPlugin> plugins) {
        for (PaymentPlugin plugin : plugins) {
            pluginMap.put(plugin.getPayCode(), plugin);
        }
    }

    public PaymentPlugin getPlugin(String code) {
        PaymentPlugin plugin = pluginMap.get(code);
        if (plugin == null) {
            throw new RuntimeException("Unsupported payment: " + code);
        }
        return plugin;
    }

    public Map<String, PaymentPlugin> getPlugins() {
        return pluginMap;
    }
}
