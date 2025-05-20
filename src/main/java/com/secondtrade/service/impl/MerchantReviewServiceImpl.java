package com.secondtrade.service.impl;

import com.secondtrade.dao.MerchantReviewDao;
import com.secondtrade.dto.MerchantReviewDTO;
import com.secondtrade.service.MerchantReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MerchantReviewServiceImpl implements MerchantReviewService {
    
    @Autowired
    private MerchantReviewDao merchantReviewDao;

    @Override
    public List<MerchantReviewDTO> getMerchantCommentsByUserId(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return merchantReviewDao.getMerchantCommentsByUserId(userId, offset, pageSize);
    }

    @Override
    public int countMerchantCommentsByUserId(Long userId) {
        return merchantReviewDao.countMerchantCommentsByUserId(userId);
    }
} 