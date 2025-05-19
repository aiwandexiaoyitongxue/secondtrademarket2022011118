package com.secondtrade.webcontroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.secondtrade.common.Result;
import com.secondtrade.entity.Order;
import com.secondtrade.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("获取订单列表")
    @GetMapping("/list")
    public Result<Object> getOrderList(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.getOrderList(
            merchantId, null, status, null, null, null, null, page, size));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @ApiOperation("更新订单状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        orderService.updateOrderStatus(id, status);
        return Result.success(null);
    }

    @ApiOperation("获取订单统计信息")
    @GetMapping("/statistics")
    public Result<Object> getOrderStatistics(@RequestParam Long merchantId) {
        return Result.success(orderService.getOrderStatistics(merchantId));
    }

    @ApiOperation("获取退款申请订单列表")
    @GetMapping("/refund/apply")
    public Result<List<Order>> getRefundApplyOrders() {
        return Result.success(orderService.getRefundApplyOrders());
    }

    @ApiOperation("获取已退款订单列表")
    @GetMapping("/refund/refunded")
    public Result<List<Order>> getRefundedOrders() {
        return Result.success(orderService.getRefundedOrders());
    }

    @ApiOperation("驳回退款")
    @PutMapping("/{id}/refund/reject")
    public Result<Void> rejectRefund(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        orderService.rejectRefund(id, reason);
        return Result.success(null);
    }

    @ApiOperation("删除驳回原因")
    @DeleteMapping("/{id}/reject-reason")
    public Result<Void> deleteRejectReason(@PathVariable Long id) {
        orderService.rejectRefund(id, null);
        return Result.success(null);
    }
} 