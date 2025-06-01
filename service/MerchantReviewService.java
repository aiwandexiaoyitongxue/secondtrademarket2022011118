package com.secondtrade.service;

import com.secondtrade.entity.MerchantReview;
import com.secondtrade.dto.MerchantReviewDTO;
import java.util.List;
import java.util.Map;

public interface MerchantReviewService {
    List<MerchantReview> getMerchantReviews(Long merchantId, Boolean hasReply, int page, int size);
    int countMerchantReviews(Long merchantId, Boolean hasReply);
    Map<String, Object> getMerchantReviewStatistics(Long merchantId);
    void replyReview(Long reviewId, Long merchantId, String reply);
    List<MerchantReviewDTO> getUserReceivedReviews(Long userId, int page, int size);
    int countUserReceivedReviews(Long userId);
    /**
     * 通过订单ID、用户ID、商家ID回复商家评价
     */
    boolean replyReviewByOrderUserMerchant(Long orderId, Long userId, Long merchantId, String reply);
}