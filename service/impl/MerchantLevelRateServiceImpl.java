package com.secondtrade.service.impl;

import com.secondtrade.dao.MerchantLevelRateMapper;
import com.secondtrade.entity.MerchantLevelRate;
import com.secondtrade.service.MerchantLevelRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantLevelRateServiceImpl implements MerchantLevelRateService {
    @Autowired
    private MerchantLevelRateMapper mapper;

    @Override
    public List<MerchantLevelRate> getAllRates() {
        return mapper.selectAll();
    }

    @Override
    public void updateRate(MerchantLevelRate rate) {
        mapper.updateRate(rate);
    }
}