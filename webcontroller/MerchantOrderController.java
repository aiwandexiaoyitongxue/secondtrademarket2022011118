package com.secondtrade.webcontroller;

import com.secondtrade.entity.Order;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.OrderService;
import com.secondtrade.service.OrderItemService;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/order")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private MerchantService merchantService;

    // 获取商家订单列表
    @GetMapping("/list")
    public ResponseEntity<?> getOrderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            List<Order> orders = orderService.getMerchantOrders(merchantId, status, page, size);
            int total = orderService.countMerchantOrders(merchantId, status);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "records", orders,
                    "total", total
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取订单列表失败: " + e.getMessage()));
        }
    }

    // 获取订单统计信息
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            Map<String, Object> stats = orderService.getMerchantOrderStatistics(merchantId);
            return ResponseEntity.ok(Map.of("success", true, "data", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取订单统计失败: " + e.getMessage()));
        }
    }

    // 获取退款申请列表
    @GetMapping("/refund/apply")
    public ResponseEntity<?> getRefundApplyList(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            List<Order> refundOrders = orderService.getMerchantRefundApplyOrders(merchantId);
            return ResponseEntity.ok(Map.of("success", true, "data", refundOrders));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取退款申请列表失败: " + e.getMessage()));
        }
    }

    // 获取已退款订单列表
    @GetMapping("/refund/refunded")
    public ResponseEntity<?> getRefundedList(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            List<Order> refundedOrders = orderService.getMerchantRefundedOrders(merchantId);
            return ResponseEntity.ok(Map.of("success", true, "data", refundedOrders));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取已退款订单列表失败: " + e.getMessage()));
        }
    }

    // 处理退款申请
    @PostMapping("/refund/{orderId}/process")
    public ResponseEntity<?> processRefund(
            @PathVariable Long orderId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reason,
            Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            orderService.processRefund(orderId, merchantId, approved, reason);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", approved ? "已同意退款申请" : "已拒绝退款申请"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "处理退款申请失败: " + e.getMessage()));
        }
    }
    // 获取订单详情
    @GetMapping("/detail")
    public ResponseEntity<?> getOrderDetail(@RequestParam Long id, Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();
            Order order = orderService.getOrderByIdAndMerchantId(id, merchantId);
            if (order == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "订单不存在或无权限"));
            }
            // 可选：查订单商品明细
            List<OrderItem> items = orderItemService.getItemsByOrderId(id);
            order.setItems(items);
            return ResponseEntity.ok(Map.of("success", true, "data", order));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取订单详情失败: " + e.getMessage()));
        }
    }

    // 更新订单状态 (发货等)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status,
            Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            com.secondtrade.entity.Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            Long merchantId = merchant.getId();

            // 验证订单是否存在且属于当前商家
            Order order = orderService.getOrderByIdAndMerchantId(orderId, merchantId);
            if (order == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "订单不存在或无权限"));
            }

            // 调用 Service 层更新订单状态
            orderService.updateOrderStatus(orderId, status);

            return ResponseEntity.ok(Map.of("success", true, "message", "订单状态更新成功"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "更新订单状态失败: " + e.getMessage()));
        }
    }
} 