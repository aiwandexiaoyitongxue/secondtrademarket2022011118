package com.secondtrade.service;

import com.secondtrade.entity.Order;
import com.secondtrade.entity.OrderItem;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order getOrderByIdAndUserId(Long orderId, Long userId);
    
    // 新增商家订单相关方法
    List<Order> getMerchantOrders(Long merchantId, Integer status, int page, int size);
    int countMerchantOrders(Long merchantId, Integer status);
    Map<String, Object> getMerchantOrderStatistics(Long merchantId);
    List<Order> getMerchantRefundApplyOrders(Long merchantId);
    List<Order> getMerchantRefundedOrders(Long merchantId);
    void processRefund(Long orderId, Long merchantId, boolean approved, String reason) throws Exception;
    
    // 原有方法
    void applyReturn(Long orderId, Long orderItemId, Long userId) throws Exception;
    void auditReturn(Long orderId, Long orderItemId, Long merchantId, boolean approved) throws Exception;
    Integer getReturnStatus(Long orderId, Long orderItemId, Long userId) throws Exception;
    void confirmReceipt(Long orderId, Long userId) throws Exception;
    Order getOrderByIdAndMerchantId(Long orderId, Long merchantId);
    // 添加 checkout 方法
    void checkout(Long userId, Long productId, Integer quantity, String tradeType,
                 Long addressId, String payMethod, Boolean usePoints, Long couponId) throws Exception;
    Order getOrderByOrderNo(String orderNo);
    void updateOrderStatus(Long orderId, Integer status);
    Order getOrderById(Long orderId);
    List<Order> getUserOrders(Long userId, Integer status, int page, int size);
    
    // 批量创建订单
    void batchCheckout(Long userId, List<Map<String, Object>> items, String tradeType,
                      Long addressId, String payMethod, Boolean usePoints, Long couponId) throws Exception;

    // 新增方法：根据订单ID获取订单项列表
    List<OrderItem> getOrderItemsByOrderId(Long orderId);

    /**
     * 完成订单
     * @param orderId 订单ID
     */
    void completeOrder(Long orderId);

    /**
     * 支付订单
     * @param orderId 订单ID
     * @param userId 用户ID
     * @throws Exception 支付失败时抛出异常
     */
    void payOrder(Long orderId, Long userId) throws Exception;
} 