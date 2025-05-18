package com.secondtrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    /**
     * 插入商家信息
     */
    int insert(Merchant merchant);

    /**
     * 根据用户ID查询商家信息
     */
    Merchant findByUserId(Long userId);

    /**
     * 更新商家信息
     */
    int update(Merchant merchant);

    /**
     * 根据ID查询商家信息
     */
    Merchant findById(Long id);
} 