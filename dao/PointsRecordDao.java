package com.secondtrade.dao;

import com.secondtrade.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import java.util.List;

@Mapper
public interface PointsRecordDao {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);

    // 新增方法：插入积分记录
    @Insert("INSERT INTO points_record (user_id, `change`, type, order_id, remark, created_time) " +
            "VALUES (#{userId}, #{change}, #{type}, #{orderId}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointsRecord pointsRecord);
}