package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.entity.User;
import com.secondtrade.service.MerchantService;
import com.secondtrade.service.UserService;
import com.secondtrade.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.secondtrade.common.Result;
import org.springframework.security.core.Authentication;
import com.secondtrade.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.secondtrade.service.ProductService;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/merchant")
public class MerchantInfoController {

    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    /**
     * 获取当前登录商家的信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getMerchantInfo(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "用户未认证"));
            }

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            if (securityUser == null || securityUser.getUser() == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "用户信息无效"));
            }

            Long userId = securityUser.getUser().getId();
            if (userId == null) {
                return ResponseEntity.status(400)
                    .body(Map.of("success", false, "message", "用户ID无效"));
            }

            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(400)
                    .body(Map.of("success", false, "message", "商家信息不存在，请先完善商家信息"));
            }

            // 统计总销售额
            try {
                Double totalSales = productService.getTotalSalesByMerchantId(merchant.getId());
                if (totalSales != null) {
                    merchant.setTotalSales(BigDecimal.valueOf(totalSales));
                }
            } catch (Exception e) {
                // 如果获取销售额失败，不影响返回商家基本信息
                System.err.println("获取销售额失败: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of("success", true, "data", merchant));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取商家信息失败: " + e.getMessage()));
        }
    }

    /**
     * 创建商家信息
     */
    @PostMapping("/create")
    public ResponseEntity<?> createMerchantInfo(
            @RequestParam("businessLicense") String businessLicense,
            @RequestParam("idCard") String idCard,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            
            // 检查是否已经存在商家信息
            Merchant existingMerchant = merchantService.getMerchantByUserId(userId);
            if (existingMerchant != null) {
                return ResponseEntity.status(400)
                    .body(Map.of("success", false, "message", "商家信息已存在"));
            }
            
            // 创建新的商家信息
            Merchant merchant = new Merchant();
            merchant.setUserId(userId);
            merchant.setBusinessLicense(businessLicense);
            merchant.setIdCard(idCard);
            merchant.setName(name);
            merchant.setDescription(description);
            merchant.setLevel(1);
            merchant.setStatus(0); // 设置为待审核状态
            merchant.setTotalSales(BigDecimal.ZERO);
            merchant.setSatisfactionRate(BigDecimal.valueOf(5.0));
            
            merchantService.createMerchant(merchant);
            
            return ResponseEntity.ok(Map.of("success", true, "message", "商家信息创建成功，等待审核"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "创建商家信息失败: " + e.getMessage()));
        }
    }
}