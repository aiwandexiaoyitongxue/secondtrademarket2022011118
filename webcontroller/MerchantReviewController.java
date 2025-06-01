package com.secondtrade.webcontroller;

import com.secondtrade.entity.ProductReview;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.ProductReviewService;
import com.secondtrade.service.MerchantService;
import com.secondtrade.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/review")
public class MerchantReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @Autowired
    private MerchantService merchantService;

    /**
     * 获取商家收到的所有评价
     */
    @GetMapping("/list-merchant-reviews")
    public ResponseEntity<?> getMerchantReviews(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            List<ProductReview> reviews = productReviewService.getReviewsByMerchantId(merchant.getId());
            return ResponseEntity.ok(Map.of("success", true, "data", reviews));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取评价列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取商家评价统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getReviewStatistics(Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }
            List<ProductReview> reviews = productReviewService.getReviewsByMerchantId(merchant.getId());
            
            // 计算平均评分和总评价数
            double avgRating = reviews.stream()
                .mapToInt(ProductReview::getRating)
                .average()
                .orElse(0.0);
            
            Map<String, Object> statistics = Map.of(
                "avgRating", avgRating,
                "totalReviews", reviews.size()
            );
            
            return ResponseEntity.ok(Map.of("success", true, "data", statistics));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取评价统计失败: " + e.getMessage()));
        }
    }
}