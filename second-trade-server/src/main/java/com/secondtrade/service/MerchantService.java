package com.secondtrade.service;

import com.secondtrade.entity.Merchant;

public interface MerchantService {
    /**
     * 创建商家信息
     */
    void create(Merchant merchant);

    /**
     * 根据用户ID查询商家信息
     */
    Merchant getByUserId(Long userId);

    /**
     * 更新商家信息
     */
    void update(Merchant merchant);

    /**
     * 根据ID查询商家信息
     */
    Merchant getById(Long id);
} 