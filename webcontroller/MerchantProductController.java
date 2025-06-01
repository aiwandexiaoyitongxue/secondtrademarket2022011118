package com.secondtrade.webcontroller;

import com.secondtrade.entity.Product;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.ProductService;
import com.secondtrade.service.MerchantService;
import com.secondtrade.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/product")
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取商家商品分类统计
     */
    @GetMapping("/category-stats")
    public ResponseEntity<?> getCategoryStats(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            List<Map<String, Object>> stats = productService.getCategoryStatsByMerchantId(merchant.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取商品分类统计失败: " + e.getMessage()));
        }
    }
} 