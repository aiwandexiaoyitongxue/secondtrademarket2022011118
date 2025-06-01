package com.secondtrade.dao;

import com.secondtrade.entity.WalletRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import java.util.List;

@Mapper
public interface WalletRecordDao {
    @Select("SELECT * FROM wallet_record WHERE user_id = #{userId} AND type = #{type} ORDER BY created_time DESC")
    List<WalletRecord> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") int type);

    @Insert("INSERT INTO wallet_record (user_id, type, amount, balance, remark, created_time) " +
            "VALUES (#{userId}, #{type}, #{amount}, #{balance}, #{remark}, #{createdTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WalletRecord walletRecord);

    @Select("SELECT * FROM wallet_record WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<WalletRecord> selectAllByUserId(@Param("userId") Long userId);

    @Select("SELECT wr.* FROM wallet_record wr " +
            "JOIN merchant m ON wr.user_id = m.user_id " +
            "WHERE m.id = #{merchantId} " +
            "ORDER BY wr.created_time DESC")
    List<WalletRecord> selectAllByMerchantId(@Param("merchantId") Long merchantId);
}