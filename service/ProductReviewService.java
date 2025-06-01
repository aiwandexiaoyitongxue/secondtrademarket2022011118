package com.secondtrade.service;

import com.secondtrade.dto.MyReviewDTO;
import com.secondtrade.entity.ProductReview;
import java.util.List;

public interface ProductReviewService {
    List<MyReviewDTO> getMyReviewsByUserId(Long userId);
    void addReview(Long userId, Long orderId, Long orderItemId, Integer rating, String content, Integer serviceRating);

    /**
     * 根据商品ID获取商品评价列表
     */
    List<ProductReview> getReviewsByProductId(Long productId);

    /**
     * 根据商品ID获取商品平均评分
     */
    Double getAverageRatingByProductId(Long productId);

    /**
     * 根据商家ID获取该商家下所有商品评价
     */
    List<ProductReview> getReviewsByMerchantId(Long merchantId);

    /**
     * 回复商品评价
     */
    void replyReview(Long reviewId, Long merchantId, String reply, Integer merchantRating);

    /**
     * 根据用户ID查询商品评价
     */
    List<ProductReview> getReviewsByUserId(Long userId);
}