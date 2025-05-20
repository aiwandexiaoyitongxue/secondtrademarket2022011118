package com.secondtrade.dao;

import com.secondtrade.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PointsRecordDao {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<PointsRecord> selectByUserId(@Param("userId") Long userId);
}