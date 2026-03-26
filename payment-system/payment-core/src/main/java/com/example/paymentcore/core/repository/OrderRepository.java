package com.example.paymentcore.repository;

import java.util.Optional;

import com.example.paymentcore.entity.PaymentOrderEntity;

public interface OrderRepository {
    PaymentOrderEntity save(PaymentOrderEntity entity);
    Optional<PaymentOrderEntity> findByPaymentId(String paymentId);
    Optional<PaymentOrderEntity> findByOrderIdAndPayCode(String orderId, String payCode);
    boolean updateStatus(String paymentId, String toStatus, String fromStatus);
}
