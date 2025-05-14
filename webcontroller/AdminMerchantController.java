package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;

    // 查询所有商家
    @GetMapping("/all")
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    // 修改商家等级
    @PostMapping("/{id}/level")
    public String updateMerchantLevel(@PathVariable Long id, @RequestBody Integer level) {
        merchantService.updateMerchantLevel(id, level);
        return "success";
    }
     // 获取待审核商家列表
    @GetMapping("/pending")
    public List<Merchant> getPendingMerchants() {
        return merchantService.getPendingMerchants();
    }
        // 审核通过
    @PostMapping("/{id}/approve")
    public String approveMerchant(@PathVariable Long id) {
        merchantService.approveMerchant(id);
        return "success";
    }
        // 审核驳回
    @PostMapping("/{id}/reject")
    public String rejectMerchant(@PathVariable Long id) {
        merchantService.rejectMerchant(id);
        return "success";
    }
}