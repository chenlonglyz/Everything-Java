package com.example.paymentcore;

import java.util.ArrayList;
import java.util.List;

import com.example.paymentapi.PaymentLink;
import com.example.paymentapi.PaymentMethod;
import com.example.paymentapi.PaymentPlugin;
import com.example.paymentapi.PaymentRequest;
import com.example.paymentapi.PaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付核心服务
 */
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentPluginManager pluginManager;

    public PaymentService(PaymentPluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    /**
     * 获取支付方式
     */
    public List<PaymentMethod> getPaymentMethods() {

        List<PaymentMethod> list = new ArrayList<>();

        for (PaymentPlugin plugin : pluginManager.getPlugins().values()) {

            PaymentMethod method = plugin.getPaymentMethod();

            list.add(method);
        }
        return list;
    }

    /**
     * 创建支付
     */
    public PaymentLink createPayment(String payCode, PaymentRequest request) {

        PaymentPlugin plugin = pluginManager.getPlugin(payCode);

        if (plugin == null) {
            throw new RuntimeException("unsupported payment");
        }

        return plugin.createPayment(request);
    }

    /**
     * 处理回调
     */
    public PaymentResult callback(String payCode, String body) {

        PaymentPlugin plugin = pluginManager.getPlugin(payCode);

        try {
            return plugin.callback(body);
        } catch (Exception e) {

            log.error("plugin error: {}", payCode, e);

            PaymentResult result = new PaymentResult();
            result.setStatus("FAIL");

            return result;
        }
    }

}
