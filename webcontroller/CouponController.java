package com.secondtrade.webcontroller;

import com.secondtrade.entity.Coupon;
import com.secondtrade.entity.CouponTemplate;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.CouponService;
import com.secondtrade.service.CouponTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponTemplateService couponTemplateService;

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

    // 获取可领取的优惠券模板
    @GetMapping("/available-to-claim")
    public ResponseEntity<?> getAvailableToClaim(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        // 1. 查询所有模板
        List<CouponTemplate> templates = couponTemplateService.getAvailableTemplates();
        // 2. 查询用户已领取的模板id
        List<Coupon> userCoupons = couponService.getAvailableCoupons(userId);
        Set<Long> ownedTemplateIds = userCoupons.stream()
            .map(Coupon::getTemplateId) // 你需要在coupon表加template_id字段
            .collect(Collectors.toSet());
        // 3. 过滤掉已领取的
        List<CouponTemplate> canClaim = templates.stream()
            .filter(t -> !ownedTemplateIds.contains(t.getId()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("success", true, "templates", canClaim));
    }

    // 领取优惠券
    @PostMapping("/claim")
    public ResponseEntity<?> claimCoupon(@RequestBody Map<String, Object> req, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Long templateId = Long.valueOf(req.get("templateId").toString());
        CouponTemplate template = couponTemplateService.getTemplateById(templateId);
        if (template == null) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "优惠券不存在"));
        // 检查是否已领取
        List<Coupon> userCoupons = couponService.getAvailableCoupons(userId);
        boolean alreadyClaimed = userCoupons.stream().anyMatch(c -> c.getTemplateId().equals(templateId));
        if (alreadyClaimed) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "已领取"));
        // 插入coupon表
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.setAmount(BigDecimal.valueOf(template.getAmount()));
        coupon.setMinAmount(BigDecimal.valueOf(template.getMinAmount()));
        coupon.setStartTime(template.getStartTime());
        coupon.setEndTime(template.getEndTime());
        coupon.setStatus(0);
        coupon.setCreatedTime(new Date());
        coupon.setTemplateId(templateId);
        couponService.saveCoupon(coupon); // 你需要在CouponService加saveCoupon方法
        couponTemplateService.incrementReceivedCount(templateId);
        return ResponseEntity.ok(Map.of("success", true, "message", "领取成功"));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserCoupons(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<Coupon> coupons = couponService.getUserCoupons(userId);
        return ResponseEntity.ok(Map.of("success", true, "coupons", coupons));
    }

    @GetMapping("/check/{couponId}")
    public ResponseEntity<?> checkCoupon(@PathVariable Long couponId, Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Coupon coupon = couponService.getCouponByIdAndUserId(couponId, userId);
        if (coupon == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "优惠券不存在"));
        }
        Date now = new Date();
        if (now.before(coupon.getStartTime()) || now.after(coupon.getEndTime())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "优惠券已过期"));
        }
        if (coupon.getStatus() != 0) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "优惠券已使用"));
        }
        return ResponseEntity.ok(Map.of("success", true, "coupon", coupon));
    }

    @PostMapping("/init")
    public ResponseEntity<?> initUserCoupons(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            couponService.initUserCoupons(userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "优惠券初始化成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/new-user")
    public ResponseEntity<?> getNewUserCoupons(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        try {
            couponService.initUserCoupons(userId);
            return ResponseEntity.ok(Map.of("success", true, "message", "获取新用户优惠券成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}