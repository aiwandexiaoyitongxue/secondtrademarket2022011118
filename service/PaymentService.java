package com.secondtrade.service;

import java.math.BigDecimal;

public interface PaymentService {
     void processPayment(String orderNo, String paymentMethod) throws Exception;
    Double calculatePointsDeduction(Long userId, Double totalAmount);
    void processRefund(Long orderId, Long userId) throws Exception;
}