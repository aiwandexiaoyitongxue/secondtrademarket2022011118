package com.secondtrade.service;

import com.secondtrade.dto.MyReviewDTO;
import java.util.List;

public interface ProductReviewService {
    List<MyReviewDTO> getMyReviewsByUserId(Long userId);
    void addReview(Long userId, Long orderId, Long orderItemId, Integer rating, String content, Integer serviceRating);
}