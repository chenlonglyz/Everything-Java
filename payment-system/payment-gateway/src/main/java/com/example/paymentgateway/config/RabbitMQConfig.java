package com.example.paymentgateway;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RabbitMQConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_CALLBACK_QUEUE = "payment.callback.queue";
    public static final String PAYMENT_CALLBACK_ROUTING_KEY = "payment.callback";

    // 1. 声明交换机
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    // 2. 声明队列
    @Bean
    public Queue callbackQueue() {
        return new Queue(PAYMENT_CALLBACK_QUEUE, true); // true=持久化
    }

    // 3. 绑定队列到交换机，指定路由键
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(callbackQueue())
                .to(paymentExchange())
                .with(PAYMENT_CALLBACK_ROUTING_KEY);  // 路由键
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {

        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // 🔥 JSON 序列化（推荐）
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        // 🔥 Confirm 回调（是否到达交换机）
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 发送失败
                log.error("Message send to exchange failed: {}", cause);
            }
        });

        // 🔥 Return 回调（是否路由到队列）
        template.setReturnsCallback(returned -> {
            log.error("Message route failed: {}", returned.getMessage());
        });

        return template;
    }
}
