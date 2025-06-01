package com.secondtrade.service.impl;

import com.secondtrade.entity.MerchantReview;
import com.secondtrade.dao.MerchantReviewDao;
import com.secondtrade.service.MerchantReviewService;
import com.secondtrade.dto.MerchantReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

@Service
public class MerchantReviewServiceImpl implements MerchantReviewService {

    @Autowired
    private MerchantReviewDao merchantReviewDao;

    @Override
    public List<MerchantReview> getMerchantReviews(Long merchantId, Boolean hasReply, int page, int size) {
        int offset = (page - 1) * size;
        return merchantReviewDao.selectByMerchantId(merchantId, hasReply, offset, size);
    }

    @Override
    public int countMerchantReviews(Long merchantId, Boolean hasReply) {
        return merchantReviewDao.countByMerchantId(merchantId, hasReply);
    }

    @Override
    public Map<String, Object> getMerchantReviewStatistics(Long merchantId) {
        Map<String, Object> stats = new HashMap<>();
        Double avgRating = merchantReviewDao.getAverageRating(merchantId);
        Integer totalReviews = merchantReviewDao.countByMerchantId(merchantId, null);
        stats.put("avgRating", avgRating != null ? avgRating : 0.0);
        stats.put("totalReviews", totalReviews);
        return stats;
    }

    @Override
    @Transactional
    public void replyReview(Long reviewId, Long merchantId, String reply) {
        MerchantReview review = merchantReviewDao.selectById(reviewId);
        if (review == null || !review.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("评价不存在或无权限");
        }
        if (review.getReply() != null) {
            throw new RuntimeException("该评价已回复");
        }
        merchantReviewDao.updateReply(reviewId, reply);
    }

    @Override
    public List<MerchantReviewDTO> getUserReceivedReviews(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        return merchantReviewDao.getMerchantCommentsByUserId(userId, offset, size);
    }

    @Override
    public int countUserReceivedReviews(Long userId) {
        return merchantReviewDao.countMerchantCommentsByUserId(userId);
    }

    @Override
    public boolean replyReviewByOrderUserMerchant(Long orderId, Long userId, Long merchantId, String reply) {
        MerchantReview review = merchantReviewDao.selectByOrderUserMerchant(orderId, userId, merchantId);
        if (review == null) {
            return false;
        }
        review.setReply(reply);
        review.setReplyTime(LocalDateTime.now());
        merchantReviewDao.updateReplyById(review.getId(), reply, review.getReplyTime());
        return true;
    }
} 