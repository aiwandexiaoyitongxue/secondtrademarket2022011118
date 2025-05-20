package com.secondtrade.webcontroller;

import com.secondtrade.entity.Coupon;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    // 查询可用优惠券
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCoupons(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<Coupon> coupons = couponService.getAvailableCoupons(userId);
        return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
    }

    // 支付时使用优惠券（可在订单支付接口中调用）
    @PostMapping("/use")
    public ResponseEntity<?> useCoupon(@RequestBody Map<String, Object> req, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Long couponId = Long.valueOf(req.get("couponId").toString());
        Long orderId = Long.valueOf(req.get("orderId").toString());
        BigDecimal orderAmount = new BigDecimal(req.get("orderAmount").toString());
        couponService.useCoupon(userId, couponId, orderId, orderAmount);
        return ResponseEntity.ok(Map.of("success", true));
    }
}