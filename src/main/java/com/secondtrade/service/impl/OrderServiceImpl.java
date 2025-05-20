   package com.secondtrade.service.impl;

   import com.secondtrade.dao.OrderDao;
   import com.secondtrade.entity.Order;
   import com.secondtrade.service.OrderService;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Service;
   import com.secondtrade.dao.OrderItemDao;
   import com.secondtrade.entity.OrderItem;
   import java.util.Date;
   @Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;

    @Override
    public Order getOrderByIdAndUserId(Long orderId, Long userId) {
        return orderDao.getOrderByIdAndUserId(orderId, userId);
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
        @Override
        public void confirmReceipt(Long orderId, Long userId) throws Exception {
            Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
            if (order == null) throw new Exception("订单不存在");
            if (order.getStatus() != 2) throw new Exception("订单状态不允许确认收货");
            orderDao.updateStatusAndReceiveTime(orderId, 3, new Date(), userId); // 传 userId
        }
}