package com.secondtrade.webcontroller;
import com.secondtrade.dto.MyReviewDTO;
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
import java.util.stream.Collectors;
import com.secondtrade.entity.ProductReview;
@RestController
@RequestMapping("/api/user/review")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    public ResponseEntity<?> getMyReviews(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<MyReviewDTO> reviews = productReviewService.getMyReviewsByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", reviews));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> req, Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            Long orderId = Long.valueOf(req.get("orderId").toString());
            Long orderItemId = Long.valueOf(req.get("orderItemId").toString());
            Integer rating = Integer.valueOf(req.get("rating").toString());
            String content = req.get("content").toString();
            Integer serviceRating = 5;
            if (req.get("serviceRating") != null) {
                try {
                    serviceRating = Integer.valueOf(req.get("serviceRating").toString());
                } catch (Exception ignore) {}
            }
            productReviewService.addReview(userId, orderId, orderItemId, rating, content, serviceRating);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "添加评论失败: " + e.getMessage()));
        }
    }

    /**
     * 商家端：获取本商家所有商品评价
     */
    @GetMapping("/list-by-merchant")
    public ResponseEntity<?> getReviewsByMerchant(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Merchant merchant = merchantService.getMerchantByUserId(userId);
        if (merchant == null) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
        }
        List<ProductReview> reviews = productReviewService.getReviewsByMerchantId(merchant.getId());
        return ResponseEntity.ok(Map.of("success", true, "data", reviews));
    }

    /**
     * 获取当前用户收到的所有商品评价（含商家回复）
     */
    @GetMapping("/list-by-user")
    public ResponseEntity<?> getReviewsByUser(Authentication authentication) {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<ProductReview> reviews = productReviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", reviews));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replyReview(@PathVariable Long id, @RequestBody Map<String, Object> req, Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            Merchant merchant = merchantService.getMerchantByUserId(userId);
            if (merchant == null) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "当前用户不是商家"));
            }

            String reply = (String) req.get("reply");
            Integer merchantRating = (Integer) req.get("merchantRating");
            if (merchantRating == null) {
                merchantRating = 5; // 默认评分
            }

            productReviewService.replyReview(id, merchant.getId(), reply, merchantRating);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "回复失败: " + e.getMessage()));
        }
    }

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
}
