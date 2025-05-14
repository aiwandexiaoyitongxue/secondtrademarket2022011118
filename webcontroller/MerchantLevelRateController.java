package com.secondtrade.webcontroller;

import com.secondtrade.entity.MerchantLevelRate;
import com.secondtrade.service.MerchantLevelRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/merchant-level-rate")
public class MerchantLevelRateController {
    @Autowired
    private MerchantLevelRateService service;

    @GetMapping
    public List<MerchantLevelRate> getAllRates() {
        return service.getAllRates();
    }

    @PostMapping("/update")
    public void updateRate(@RequestBody MerchantLevelRate rate) {
        service.updateRate(rate);
    }
}