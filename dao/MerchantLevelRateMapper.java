package com.secondtrade.dao;

import com.secondtrade.entity.MerchantLevelRate;
import org.apache.ibatis.annotations.*;
import com.secondtrade.entity.User;
import com.secondtrade.entity.Merchant;
import com.secondtrade.entity.Product;
import java.util.List;

@Mapper
public interface MerchantLevelRateMapper {
    @Select("SELECT * FROM merchant_level_rate")
    List<MerchantLevelRate> selectAll();

    @Update("UPDATE merchant_level_rate SET rate = #{rate}, description = #{description} WHERE level = #{level}")
    int updateRate(MerchantLevelRate rate);
}