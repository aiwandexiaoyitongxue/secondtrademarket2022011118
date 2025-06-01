package com.secondtrade.webcontroller;

import com.secondtrade.entity.Order;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.entity.Merchant;
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
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private MerchantService merchantService;

    // 订单详情接口
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long id, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUser().getId();
        Integer role = securityUser.getUser().getRole();
        
        Order order;
        if (role == 1) { // 商家角色
            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "商家信息不存在"));
            }
            order = orderService.getOrderByIdAndMerchantId(id, merchant.getId());
        } else {
            order = orderService.getOrderByIdAndUserId(id, userId);
        }
        
        if (order == null) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "订单不存在"));
        }
        List<OrderItem> items = orderItemService.getItemsByOrderId(order.getId());
        order.setItems(items);
        return ResponseEntity.ok(Map.of("success", true, "data", order));
    }
    // 退货申请
    @PostMapping("/return")
        public ResponseEntity<?> applyReturn(@RequestParam Long orderId, @RequestParam Long orderItemId, Authentication authentication) {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            try {
                orderService.applyReturn(orderId, orderItemId, userId);
                return ResponseEntity.ok(Map.of("success", true, "message", "退货申请已提交，请等待商家进行审核"));
            } catch (Exception e) {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
            }
        }
    // 退货审核
    @PostMapping("/return/audit")
    public ResponseEntity<?> auditReturn(@RequestParam Long orderId, @RequestParam Long orderItemId, @RequestParam boolean approved, Authentication authentication) {
        Long merchantId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            orderService.auditReturn(orderId, orderItemId, merchantId, approved);
            return ResponseEntity.ok(Map.of("success", true, "message", approved ? "退货申请已通过" : "退货申请已拒绝"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    // 退货状态查询
    @GetMapping("/return/status")
    public ResponseEntity<?> getReturnStatus(@RequestParam Long orderId, @RequestParam Long orderItemId, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            Integer status = orderService.getReturnStatus(orderId, orderItemId, userId);
            return ResponseEntity.ok(Map.of("success", true, "data", status));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReceipt(@RequestParam Long orderId, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            orderService.confirmReceipt(orderId, userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "确认收货成功"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody Map<String, Object> req, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            // 解析参数
            List<Map<String, Object>> items = (List<Map<String, Object>>) req.get("items");
            String tradeType = req.get("tradeType").toString();
            Long addressId = req.get("addressId") == null ? null : Long.valueOf(req.get("addressId").toString());
            String payMethod = req.get("payMethod").toString();
            Boolean usePoints = Boolean.valueOf(req.get("usePoints").toString());
            Long couponId = req.get("couponId") == null ? null : Long.valueOf(req.get("couponId").toString());

            // 调用 service 处理批量下单
            orderService.batchCheckout(userId, items, tradeType, addressId, payMethod, usePoints, couponId);

            return ResponseEntity.ok(Map.of("success", true, "message", "下单成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "下单失败: " + e.getMessage()));
        }
    }
    @GetMapping
    public ResponseEntity<?> getOrderList(
        @RequestParam(required = false) Integer status,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long merchantId,
        Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            List<Order> orders;
            if (merchantId != null) {
                orders = orderService.getMerchantOrders(merchantId, status, page, size);
            } else {
                orders = orderService.getUserOrders(userId, status, page, size);
            }
            return ResponseEntity.ok(Map.of("success", true, "data", orders));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // 支付订单接口
    @PostMapping("/pay")
    public ResponseEntity<?> payOrder(@RequestParam Long orderId, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            orderService.payOrder(orderId, userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "支付成功"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}