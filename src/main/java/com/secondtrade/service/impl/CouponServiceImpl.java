package com.secondtrade.service.impl;

import com.secondtrade.dao.CouponDao;
import com.secondtrade.entity.Coupon;
import com.secondtrade.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponDao couponDao;

    @Override
    public List<Coupon> getAvailableCoupons(Long userId) {
        return couponDao.selectAvailableCoupons(userId, new Date());
    }

    @Override
    @Transactional
    public void useCoupon(Long userId, Long couponId, Long orderId, BigDecimal orderAmount) {
        Coupon coupon = couponDao.selectById(couponId);
        if (coupon == null || !coupon.getUserId().equals(userId) || coupon.getStatus() != 0) {
            throw new RuntimeException("优惠券不可用");
        }
        Date now = new Date();
        if (now.before(coupon.getStartTime()) || now.after(coupon.getEndTime())) {
            throw new RuntimeException("优惠券已过期");
        }
        if (orderAmount.compareTo(coupon.getMinAmount()) < 0) {
            throw new RuntimeException("未满足优惠券最低消费金额");
        }
        couponDao.updateStatus(couponId, 1); // 1-已使用
        // 这里建议在订单表加 coupon_id 字段，并在订单支付逻辑中减去优惠券金额
    }
}