package com.secondtrade.dao;

import com.secondtrade.entity.CouponTemplate;
import org.apache.ibatis.annotations.*;
import java.util.Date;
import java.util.List;

@Mapper
public interface CouponTemplateDao {
    @Select("SELECT * FROM coupon_template WHERE start_time <= #{now} AND end_time >= #{now}")
    List<CouponTemplate> selectAvailableTemplates(@Param("now") Date now);

    @Update("UPDATE coupon_template SET received_count = received_count + 1 WHERE id = #{id}")
    int incrementReceivedCount(@Param("id") Long id);

    @Select("SELECT * FROM coupon_template WHERE id = #{id}")
    CouponTemplate selectById(@Param("id") Long id);
}