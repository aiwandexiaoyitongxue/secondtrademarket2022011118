package com.secondtrade.webcontroller;

import com.secondtrade.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("userCount", adminStatisticsService.getUserCount());
        result.put("merchantCount", adminStatisticsService.getMerchantCount());
        result.put("productCount", adminStatisticsService.getProductCount());
        result.put("todayAmount", adminStatisticsService.getTodayAmount());
         result.put("pendingUserCount", adminStatisticsService.getPendingUserCount());
        return result;
    }
   
}