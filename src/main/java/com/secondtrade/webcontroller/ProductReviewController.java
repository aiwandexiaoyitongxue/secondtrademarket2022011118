  package com.secondtrade.webcontroller;
import com.secondtrade.dto.MyReviewDTO;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/review")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

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
        Long orderItemId = Long.valueOf(req.get("orderItemId").toString()); // 新增
        Integer rating = Integer.valueOf(req.get("rating").toString());
        String content = req.get("content").toString();
        Integer serviceRating = Integer.valueOf(req.get("serviceRating").toString());
        productReviewService.addReview(userId, orderId, orderItemId, rating, content, serviceRating);
        return ResponseEntity.ok(Map.of("success", true));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
            .body(Map.of("success", false, "message", "添加评论失败: " + e.getMessage()));
    }
}
}
