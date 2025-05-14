package com.secondtrade.service;

import com.secondtrade.entity.MerchantLevelRate;
import java.util.List;

public interface MerchantLevelRateService {
    List<MerchantLevelRate> getAllRates();
    void updateRate(MerchantLevelRate rate);
}