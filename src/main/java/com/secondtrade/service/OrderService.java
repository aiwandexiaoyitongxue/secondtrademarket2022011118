package com.secondtrade.service;

import com.secondtrade.entity.Order;

public interface OrderService {
    Order getOrderByIdAndUserId(Long orderId, Long userId);
    // 新增方法
    void applyReturn(Long orderId, Long orderItemId, Long userId) throws Exception;
    void auditReturn(Long orderId, Long orderItemId, Long merchantId, boolean approved) throws Exception;
    Integer getReturnStatus(Long orderId, Long orderItemId, Long userId) throws Exception;
    void confirmReceipt(Long orderId, Long userId) throws Exception;
}