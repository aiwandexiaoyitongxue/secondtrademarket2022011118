package com.secondtrade.service.impl;

import com.secondtrade.dao.MerchantLevelRateMapper;
import com.secondtrade.entity.MerchantLevelRate;
import com.secondtrade.service.MerchantLevelRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class MerchantLevelRateServiceImpl implements MerchantLevelRateService {
    private static final Logger logger = LoggerFactory.getLogger(MerchantLevelRateServiceImpl.class);
    
    @Autowired
    private MerchantLevelRateMapper mapper;

    @Override
    public List<MerchantLevelRate> getAllRates() {
        try {
            return mapper.selectAll();
        } catch (Exception e) {
            logger.error("Failed to get all merchant level rates", e);
            throw new RuntimeException("获取商家等级费率列表失败", e);
        }
    }

    @Override
    public void updateRate(MerchantLevelRate rate) {
        try {
            int rows = mapper.updateRate(rate);
            if (rows == 0) {
                throw new RuntimeException("未找到对应的商家等级费率记录");
            }
            logger.info("Successfully updated merchant level rate for level {}", rate.getLevel());
        } catch (Exception e) {
            logger.error("Failed to update merchant level rate", e);
            throw new RuntimeException("更新商家等级费率失败", e);
        }
    }
}