package com.secondtrade.service.impl;

import com.secondtrade.dao.OrderItemDao;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.secondtrade.dao.OrderDao;
import com.secondtrade.entity.Order;                         
@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemDao.getItemsByOrderId(orderId);
    }

    @Override
    public void applyReturn(Long orderId, Long orderItemId, Long userId) throws Exception {
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) throw new Exception("订单不存在");
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        if (order.getStatus() == null || order.getStatus() != 3) throw new Exception("订单未收货，不能申请退货");
        long now = System.currentTimeMillis();
        long receivedTime = order.getReceiveTime().getTime();
        if (now - receivedTime > 24 * 60 * 60 * 1000) throw new Exception("收货已超过24小时，不能申请退货");
        if (item.getRefundStatus() != null && item.getRefundStatus() == 1) throw new Exception("该订单项已申请退货");
        orderItemDao.updateRefundStatus(orderItemId, 1); // 1=申请中
    }

    @Override
    public void auditReturn(Long orderId, Long orderItemId, Long merchantId, boolean approved) throws Exception {
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        if (!item.getMerchantId().equals(merchantId)) throw new Exception("无权操作此订单");
        if (item.getRefundStatus() == null || item.getRefundStatus() != 1) throw new Exception("该订单项未申请退货");
        if (approved) {
            orderItemDao.updateRefundStatus(orderItemId, 2); // 2=已同意
        } else {
            orderItemDao.updateRefundStatus(orderItemId, 3); // 3=已拒绝
        }
    }

    @Override
    public Integer getReturnStatus(Long orderId, Long orderItemId, Long userId) throws Exception {
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) throw new Exception("订单不存在");
        OrderItem item = orderItemDao.getById(orderItemId);
        if (item == null || !item.getOrderId().equals(orderId)) throw new Exception("订单项不存在");
        return item.getRefundStatus();
    }
}