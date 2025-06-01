package com.secondtrade.service.impl;

import com.secondtrade.service.PaymentService;
import com.secondtrade.service.OrderService;
import com.secondtrade.service.UserService;
import com.secondtrade.service.ShoppingCartService;
import com.secondtrade.service.PointsRecordService;
import com.secondtrade.entity.Order;
import com.secondtrade.entity.User;
import com.secondtrade.entity.PointsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private PointsRecordService pointsRecordService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processPayment(String orderNo, String paymentMethod) throws Exception {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            throw new Exception("订单不存在书");
        }
        
        try {
            if ("balance".equals(paymentMethod)) {
                User user = userService.getUserById(order.getUserId());
                if (user == null) {
                    throw new Exception("用户不存在");
                }
                if (order.getPayAmount() == null || user.getWalletBalance() < order.getPayAmount()) {
                    throw new Exception("余额不足或支付金额无效");
                }
                // 计算新的余额
                Double newBalance = user.getWalletBalance() - order.getPayAmount();
                userService.updateWalletBalance(order.getUserId(), newBalance);
                
            } else if ("points".equals(paymentMethod)) {
                User user = userService.getUserById(order.getUserId());
                if (user == null) {
                    throw new Exception("用户不存在");
                }
                if (order.getPayAmount() == null) {
                     throw new Exception("支付金额无效");
                }
                int requiredPoints = (int)(order.getPayAmount() * 100);
                if (user.getPoints() < requiredPoints) {
                    throw new Exception("积分不足");
                }
                userService.updatePoints(order.getUserId(), user.getPoints() - requiredPoints);

                // 添加积分变动记录 (消费)
                 if (requiredPoints > 0) { // 只记录消费积分大于0的情况
                     PointsRecord pointsRecord = new PointsRecord();
                     pointsRecord.setUserId(order.getUserId());
                     pointsRecord.setChange(-requiredPoints); // 消费为负数
                     pointsRecord.setType(2); // 2-消费
                     pointsRecord.setOrderId(order.getId()); // 关联订单
                     pointsRecord.setRemark("订单" + order.getOrderNo() + "积分抵扣");
                     pointsRecord.setCreatedTime(new Date());
                     pointsRecordService.addPointsRecord(pointsRecord);
                 }

            } else {
                throw new Exception("不支持的支付方式");
            }
            
            // 更新订单状态为已支付
            orderService.updateOrderStatus(order.getId(), 1);
            
            // 删除购物车中的商品
            shoppingCartService.clearCart(order.getUserId());
            
            // 增加积分（消费1元增加1积分）
            int earnedPoints = 0;
            if (order.getPayAmount() != null) {
                // 注意：这里是从 order.getPayAmount() 获取支付金额来计算赠送积分，
                // 如果是积分支付，order.getPayAmount() 可能是0或者扣除积分后的金额。
                // 赠送积分应该是基于订单的总金额 (order.getTotalAmount()) 来计算，而不是实际支付金额
                // 并且只有在非积分支付（余额支付）时才赠送积分。
                // 暂时先保留基于 PayAmount 的逻辑，如果需要修改为基于 TotalAmount，再进行调整。
                 earnedPoints = (int) Math.floor(order.getPayAmount());
            }

            // 只有在非积分支付时才赠送积分
            if (!"points".equals(paymentMethod) && earnedPoints > 0) {
                User user = userService.getUserById(order.getUserId());
                 if (user != null) {
                      userService.updatePoints(order.getUserId(), user.getPoints() + earnedPoints);

                      // 添加积分变动记录 (获得)
                      PointsRecord pointsRecord = new PointsRecord();
                      pointsRecord.setUserId(order.getUserId());
                      pointsRecord.setChange(earnedPoints); // 获得为正数
                      pointsRecord.setType(1); // 1-获得
                      pointsRecord.setOrderId(order.getId()); // 关联订单
                      pointsRecord.setRemark("订单" + order.getOrderNo() + "消费获得积分");
                      pointsRecord.setCreatedTime(new Date());
                      pointsRecordService.addPointsRecord(pointsRecord);
                 }
            }
            
        } catch (Exception e) {
            throw new Exception("支付处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public Double calculatePointsDeduction(Long userId, Double totalAmount) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return 0.0;
        }
        int availablePoints = user.getPoints();
        
        Double maxDeduction = availablePoints / 100.0; // 100积分抵扣1元
        Double maxAllowedDeduction = totalAmount * 0.3; // 最高抵扣订单金额的30%
        
        return Math.min(maxDeduction, maxAllowedDeduction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRefund(Long orderId, Long userId) throws Exception {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new Exception("无权操作此订单");
        }

        User user = userService.getUserById(userId);
         if (user == null) {
            throw new Exception("用户不存在");
        }
        
        // 退还余额或积分
        if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
            // 退还积分
            userService.updatePoints(userId, user.getPoints() + order.getPointsUsed());
             // 添加积分变动记录 (退还)
             PointsRecord pointsRecord = new PointsRecord();
             pointsRecord.setUserId(userId);
             pointsRecord.setChange(order.getPointsUsed()); // 退还为正数
             pointsRecord.setType(3); // 3-退还 (需要定义这个类型)
             pointsRecord.setOrderId(orderId); // 关联订单
             pointsRecord.setRemark("订单" + order.getOrderNo() + "退还积分");
             pointsRecord.setCreatedTime(new Date());
             pointsRecordService.addPointsRecord(pointsRecord);

        } else if (order.getPayAmount() != null && order.getPayAmount() > 0) {
            // 退还余额
            userService.updateWalletBalance(userId, 
                user.getWalletBalance() + order.getPayAmount());
        }
        
        // 更新订单状态
        orderService.updateOrderStatus(orderId, 5); // 5-已退款
    }
}