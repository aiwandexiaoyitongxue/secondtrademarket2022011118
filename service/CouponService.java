package com.secondtrade.service;

import com.secondtrade.entity.Coupon;
import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<Coupon> getAvailableCoupons(Long userId);
    void useCoupon(Long userId, Long couponId, Long orderId, BigDecimal orderAmount);
    Coupon getCouponByIdAndUserId(Long couponId, Long userId);
    void saveCoupon(Coupon coupon);
    List<Coupon> getUserCoupons(Long userId);
    void initUserCoupons(Long userId);
}