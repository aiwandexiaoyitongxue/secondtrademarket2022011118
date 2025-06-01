package com.secondtrade.service;

import com.secondtrade.entity.OrderItem;
import java.util.List;
import com.secondtrade.entity.Order;
public interface OrderItemService {
    List<OrderItem> getItemsByOrderId(Long orderId);
    void applyReturn(Long orderId, Long orderItemId, Long userId) throws Exception;

    void auditReturn(Long orderId, Long orderItemId, Long merchantId, boolean approved) throws Exception;

    Integer getReturnStatus(Long orderId, Long orderItemId, Long userId) throws Exception;
   
}