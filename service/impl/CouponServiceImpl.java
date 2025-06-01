package com.secondtrade.service.impl;

import com.secondtrade.dao.CouponDao;
import com.secondtrade.entity.Coupon;
import com.secondtrade.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
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
    public Coupon getCouponByIdAndUserId(Long couponId, Long userId) {
        Coupon coupon = couponDao.selectById(couponId);
        if (coupon != null && coupon.getUserId().equals(userId)) {
            return coupon;
        }
        return null;
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
        couponDao.updateStatusAndOrderId(couponId, userId, 1, orderId); // 1 = used
        // 这里建议在订单表加 coupon_id 字段，并在订单支付逻辑中减去优惠券金额
    }

    @Override
    public void saveCoupon(Coupon coupon) {
        couponDao.insert(coupon);
    }

    @Override
    public List<Coupon> getUserCoupons(Long userId) {
        return couponDao.selectByUserId(userId);
    }

    @Override
    @Transactional
    public void initUserCoupons(Long userId) {
        // 检查用户是否已经有优惠券
        List<Coupon> existingCoupons = couponDao.selectByUserId(userId);
        if (!existingCoupons.isEmpty()) {
            throw new RuntimeException("您已经领取过新用户优惠券了");
        }

        // 创建5张不同面额的优惠券
        createCoupon(userId, new BigDecimal("5"), new BigDecimal("50"), 30); // 5元券，满50可用，30天有效期
        createCoupon(userId, new BigDecimal("10"), new BigDecimal("100"), 30); // 10元券，满100可用，30天有效期
        createCoupon(userId, new BigDecimal("20"), new BigDecimal("200"), 30); // 20元券，满200可用，30天有效期
        createCoupon(userId, new BigDecimal("30"), new BigDecimal("300"), 30); // 30元券，满300可用，30天有效期
        createCoupon(userId, new BigDecimal("50"), new BigDecimal("500"), 30); // 50元券，满500可用，30天有效期
    }

    private void createCoupon(Long userId, BigDecimal amount, BigDecimal minAmount, int validDays) {
        Coupon coupon = new Coupon();
        coupon.setUserId(userId);
        coupon.setAmount(amount);
        coupon.setMinAmount(minAmount);
        
        // 设置生效时间为当前时间
        Date now = new Date();
        coupon.setStartTime(now);
        
        // 设置失效时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, validDays);
        coupon.setEndTime(calendar.getTime());
        
        coupon.setStatus(0); // 未使用
        coupon.setCreatedTime(now);
        
        couponDao.insert(coupon);
    }
}