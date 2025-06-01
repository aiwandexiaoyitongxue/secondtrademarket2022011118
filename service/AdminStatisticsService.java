// 路径：second-trade-server/src/main/java/com/secondtrade/service/AdminStatisticsService.java

package com.secondtrade.service;

public interface AdminStatisticsService {
    int getUserCount();
    int getMerchantCount();
    int getProductCount();
    double getTodayAmount();
    int getPendingUserCount();
}