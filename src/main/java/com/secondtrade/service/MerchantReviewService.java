package com.secondtrade.service;

import com.secondtrade.dto.MerchantReviewDTO;
import java.util.List;

public interface MerchantReviewService {
    List<MerchantReviewDTO> getMerchantCommentsByUserId(Long userId, int page, int pageSize);
    int countMerchantCommentsByUserId(Long userId);
}