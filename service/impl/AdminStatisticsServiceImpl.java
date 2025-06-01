// 路径：second-trade-server/src/main/java/com/secondtrade/service/impl/AdminStatisticsServiceImpl.java

package com.secondtrade.service.impl;

import com.secondtrade.dao.AdminStatisticsMapper;
import com.secondtrade.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    @Autowired
    private AdminStatisticsMapper adminStatisticsMapper;

    @Override
    public int getUserCount() {
        return adminStatisticsMapper.countUsers();
    }

    @Override
    public int getMerchantCount() {
        return adminStatisticsMapper.countMerchants();
    }

    @Override
    public int getProductCount() {
        return adminStatisticsMapper.countProducts();
    }

    @Override
    public double getTodayAmount() {
        return adminStatisticsMapper.sumTodayAmount();
    }
    @Override
    public int getPendingUserCount() {
        return adminStatisticsMapper.countPendingUsers();
    }
}