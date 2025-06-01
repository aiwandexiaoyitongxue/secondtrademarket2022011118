package com.secondtrade.webcontroller;

import com.secondtrade.entity.MerchantLevelRate;
import com.secondtrade.service.MerchantLevelRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/admin/merchant-level-rate")
public class MerchantLevelRateController {
    private static final Logger logger = LoggerFactory.getLogger(MerchantLevelRateController.class);
    
    @Autowired
    private MerchantLevelRateService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<MerchantLevelRate> getAllRates() {
        return service.getAllRates();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void updateRate(@RequestBody MerchantLevelRate rate) {
        try {
            logger.info("Updating merchant level rate: level={}, rate={}, description={}", 
                rate.getLevel(), rate.getRate(), rate.getDescription());
            
            if (rate.getLevel() == null || rate.getLevel() < 1 || rate.getLevel() > 5) {
                throw new IllegalArgumentException("商家等级必须在1-5之间");
            }
            
            if (rate.getRate() == null || rate.getRate() < 0 || rate.getRate() > 1) {
                throw new IllegalArgumentException("费率必须在0-1之间");
            }
            
            service.updateRate(rate);
            logger.info("Successfully updated merchant level rate for level {}", rate.getLevel());
        } catch (Exception e) {
            logger.error("Failed to update merchant level rate", e);
            throw e;
        }
    }
}