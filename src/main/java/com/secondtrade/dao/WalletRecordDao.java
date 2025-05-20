package com.secondtrade.dao;

import com.secondtrade.entity.WalletRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface WalletRecordDao {
    @Select("SELECT * FROM wallet_record WHERE user_id = #{userId} AND type = #{type} ORDER BY created_time DESC")
    List<WalletRecord> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") int type);
}