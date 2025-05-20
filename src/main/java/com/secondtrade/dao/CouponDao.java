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
}