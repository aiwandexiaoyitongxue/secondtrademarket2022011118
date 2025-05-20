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
}