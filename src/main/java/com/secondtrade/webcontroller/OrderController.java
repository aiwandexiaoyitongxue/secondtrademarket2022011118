package com.secondtrade.webcontroller;

import com.secondtrade.entity.Order;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.OrderService;
import com.secondtrade.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    // 订单详情接口
    @GetMapping("/detail")
    public ResponseEntity<?> getOrderDetail(@RequestParam Long id, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Order order = orderService.getOrderByIdAndUserId(id, userId);
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
}