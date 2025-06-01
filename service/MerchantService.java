package com.secondtrade.service;

import com.secondtrade.entity.Merchant;
import java.util.List;

public interface MerchantService {
    List<Merchant> getPendingMerchants();
    void approveMerchant(Long id);
    void rejectMerchant(Long id);
    void disableMerchant(Long id);
    List<Merchant> getAllMerchants();
    Merchant getMerchantById(Long id);
    void updateMerchantLevel(Long id, Integer level);
    void insertMerchant(Merchant merchant);
    Merchant getById(Long id);
    Merchant getMerchantByUserId(Long userId);
    void createMerchant(Merchant merchant);

    /**
     * 根据商家ID获取商家信息
     */
    Merchant getMerchantInfoById(Long merchantId);

    /**
     * 更新商家信息
     */
    void updateMerchant(Merchant merchant);
}