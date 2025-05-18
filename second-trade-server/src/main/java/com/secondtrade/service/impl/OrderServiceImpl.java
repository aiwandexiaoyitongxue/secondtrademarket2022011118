package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.entity.Order;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.mapper.OrderMapper;
import com.secondtrade.mapper.OrderItemMapper;
import com.secondtrade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Page<Order> getOrderList(Long merchantId, String orderNo, Integer status, 
                                  String startDate, String endDate, 
                                  BigDecimal minAmount, BigDecimal maxAmount,
                                  Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 基础条件：商家ID
        wrapper.eq(merchantId != null, Order::getMerchantId, merchantId);
        
        // 订单状态筛选
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(Order::getCreatedTime);
        
        // 执行分页查询
        Page<Order> pageResult = page(new Page<>(page, size), wrapper);
        
        // 打印查询条件，方便调试
        System.out.println("订单查询条件：" + wrapper.getCustomSqlSegment());
        System.out.println("查询结果数量：" + pageResult.getTotal());
        
        return pageResult;
    }

    @Override
    public Order getOrderDetail(Long id) {
        Order order = getById(id);
        if (order != null) {
            // 查询订单项
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
            order.setOrderItems(items);
        }
        return order;
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, Integer status) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdatedTime(LocalDateTime.now());
        
        // 根据状态更新相应的时间字段
        switch (status) {
            case 1: // 待发货
                order.setPayTime(LocalDateTime.now());
                break;
            case 2: // 待收货
                order.setDeliveryTime(LocalDateTime.now());
                break;
            case 3: // 已完成
                order.setReceiveTime(LocalDateTime.now());
                break;
            case 4: // 已取消
                order.setCancelTime(LocalDateTime.now());
                break;
            case 5: // 已退款
                order.setRefundTime(LocalDateTime.now());
                break;
        }
        
        updateById(order);
    }

    @Override
    public Object getOrderStatistics(Long merchantId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMerchantId, merchantId);
        
        // 获取总订单数
        long totalOrders = count(wrapper);
        
        // 获取各状态订单数
        Map<String, Long> statusCounts = new HashMap<>();
        for (int status = 0; status <= 5; status++) {
            LambdaQueryWrapper<Order> statusWrapper = new LambdaQueryWrapper<>();
            statusWrapper.eq(Order::getMerchantId, merchantId)
                        .eq(Order::getStatus, status);
            statusCounts.put("status" + status, count(statusWrapper));
        }
        
        // 获取总销售额
        wrapper.eq(Order::getStatus, 3); // 只统计已完成的订单
        double totalSales = list(wrapper).stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        
        // 获取今日订单数和销售额
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Order::getMerchantId, merchantId)
                   .ge(Order::getCreatedTime, today);
        long todayOrders = count(todayWrapper);
        
        double todaySales = list(todayWrapper).stream()
                .filter(order -> order.getStatus() == 3) // 只统计已完成的订单
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalOrders", totalOrders);
        statistics.put("statusCounts", statusCounts);
        statistics.put("totalSales", totalSales);
        statistics.put("todayOrders", todayOrders);
        statistics.put("todaySales", todaySales);
        
        return statistics;
    }
} 