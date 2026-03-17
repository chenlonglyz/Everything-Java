package com.example.paymentgateway;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.paymentapi.PaymentResult;

@Service
public class PaymentMQService {
    private final RabbitTemplate rabbitTemplate;

    public PaymentMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(PaymentResult result) {
        CorrelationData correlationData =
                new CorrelationData(result.getOrderId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_CALLBACK_ROUTING_KEY,
                result,
                correlationData
        );
    }
}