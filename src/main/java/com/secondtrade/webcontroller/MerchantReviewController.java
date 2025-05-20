package com.secondtrade.webcontroller;

import com.secondtrade.dto.MerchantReviewDTO;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.MerchantReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/merchant-comments")
public class MerchantReviewController {

    @Autowired
    private MerchantReviewService merchantReviewService;

    @GetMapping
    public ResponseEntity<?> getMerchantComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication) {
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            List<MerchantReviewDTO> comments = merchantReviewService.getMerchantCommentsByUserId(userId, page, pageSize);
            int total = merchantReviewService.countMerchantCommentsByUserId(userId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "records", comments,
                    "total", total
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("success", false, "message", "获取商家评价失败: " + e.getMessage()));
        }
    }
}