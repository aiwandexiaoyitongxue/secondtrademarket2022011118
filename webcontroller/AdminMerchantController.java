package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Map; // ← 这里要加上
@RestController
@RequestMapping("/api/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    // 查询所有商家
    @GetMapping("/all")
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    // 修改商家等级
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    @PostMapping("/{id}/level")
    public String updateMerchantLevel(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        System.out.println("收到参数: " + body);
        Integer level = body.get("level");
        merchantService.updateMerchantLevel(id, level);
        return "success";
    }
     // 获取待审核商家列表
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<Merchant> getPendingMerchants() {
        return merchantService.getPendingMerchants();
    }
        // 审核通过
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String approveMerchant(@PathVariable Long id) {
        merchantService.approveMerchant(id);
        return "success";
    }
        // 审核驳回
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String rejectMerchant(@PathVariable Long id) {
        merchantService.rejectMerchant(id);
        return "success";
    }
}