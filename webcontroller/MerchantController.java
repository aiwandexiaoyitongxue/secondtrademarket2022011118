package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import com.secondtrade.common.exception.BusinessException;
@Slf4j
@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    // 查询待审核商家
    @GetMapping("/pending")         
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
   public List<Merchant> getPendingMerchants() {
    try {
        return merchantService.getPendingMerchants();
    } catch (Exception e) {
        log.error("获取待审核商家列表失败", e);
        throw new BusinessException("获取待审核商家列表失败");
    }
}

    // 审核通过
    @PostMapping("/{id}/approve")           
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void approveMerchant(@PathVariable Long id) {
        merchantService.approveMerchant(id);
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void rejectMerchant(@PathVariable Long id) {
        merchantService.rejectMerchant(id);
    }

    // 禁用商家
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void disableMerchant(@PathVariable Long id) {
        merchantService.disableMerchant(id);
    }

    // 查询所有商家
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    // 查询单个商家
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public Merchant getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }
}