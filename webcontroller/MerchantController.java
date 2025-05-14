package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    // 查询待审核商家
    @GetMapping("/pending")
    public List<Merchant> getPendingMerchants() {
        return merchantService.getPendingMerchants();
    }

    // 审核通过
    @PostMapping("/{id}/approve")
    public void approveMerchant(@PathVariable Long id) {
        merchantService.approveMerchant(id);
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    public void rejectMerchant(@PathVariable Long id) {
        merchantService.rejectMerchant(id);
    }

    // 禁用商家
    @PostMapping("/{id}/disable")
    public void disableMerchant(@PathVariable Long id) {
        merchantService.disableMerchant(id);
    }

    // 查询所有商家
    @GetMapping
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    // 查询单个商家
    @GetMapping("/{id}")
    public Merchant getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }
}