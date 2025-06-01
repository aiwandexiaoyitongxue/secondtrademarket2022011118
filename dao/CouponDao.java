package com.secondtrade.dao;

import com.secondtrade.entity.Coupon;
import org.apache.ibatis.annotations.*;
import java.util.Date;
import java.util.List;

@Mapper
public interface CouponDao {
    @Select("SELECT * FROM coupon WHERE user_id = #{userId} AND status = 0 AND start_time <= #{now} AND end_time >= #{now}")
    List<Coupon> selectAvailableCoupons(@Param("userId") Long userId, @Param("now") Date now);

    @Select("SELECT * FROM coupon WHERE id = #{id}")
    Coupon selectById(@Param("id") Long id);

    @Update("UPDATE coupon SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE coupon SET status = #{status}, order_id = #{orderId} WHERE id = #{id} AND user_id = #{userId}")
    int updateStatusAndOrderId(@Param("id") Long id, @Param("userId") Long userId, @Param("status") Integer status, @Param("orderId") Long orderId);

    @Insert("INSERT INTO coupon (user_id, amount, min_amount, start_time, end_time, status, created_time, template_id) " +
            "VALUES (#{userId}, #{amount}, #{minAmount}, #{startTime}, #{endTime}, #{status}, #{createdTime}, #{templateId})")
    int insert(Coupon coupon);

    @Select("SELECT * FROM coupon WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Coupon> selectByUserId(@Param("userId") Long userId);
}