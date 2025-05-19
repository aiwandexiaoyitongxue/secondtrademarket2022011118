package com.secondtrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.secondtrade.entity.Order;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService extends IService<Order> {
    
    /**
     * 获取订单列表
     * @param merchantId 商家ID
     * @param orderNo 订单号
     * @param status 订单状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param page 页码
     * @param size 每页大小
     * @return 分页订单列表
     */
    Page<Order> getOrderList(Long merchantId, String orderNo, Integer status, 
                           String startDate, String endDate, 
                           BigDecimal minAmount, BigDecimal maxAmount,
                           Integer page, Integer size);

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    Order getOrderDetail(Long id);

    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态
     */
    void updateOrderStatus(Long id, Integer status);

    /**
     * 获取订单统计信息
     * @param merchantId 商家ID
     * @return 统计信息
     */
    Object getOrderStatistics(Long merchantId);

    // 获取退款申请订单列表
    List<Order> getRefundApplyOrders();

    // 获取已退款订单列表
    List<Order> getRefundedOrders();

    // 驳回退款
    void rejectRefund(Long id, String reason);
} 