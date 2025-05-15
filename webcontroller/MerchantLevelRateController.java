package com.secondtrade.webcontroller;

import com.secondtrade.entity.MerchantLevelRate;
import com.secondtrade.service.MerchantLevelRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/admin/merchant-level-rate")
public class MerchantLevelRateController {
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
        service.updateRate(rate);
    }
}