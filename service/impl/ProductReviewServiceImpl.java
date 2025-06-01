package com.secondtrade.service.impl;
import com.secondtrade.dao.ProductReviewDao;
import com.secondtrade.dto.MyReviewDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.secondtrade.service.ProductReviewService;
import com.secondtrade.entity.ProductReview;
import com.secondtrade.dao.OrderDao;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondtrade.dao.OrderItemDao;
import com.secondtrade.entity.OrderItem;
import com.secondtrade.entity.Order;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    @Autowired
    private ProductReviewDao productReviewDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderItemDao orderItemDao;
    @Override
    public List<MyReviewDTO> getMyReviewsByUserId(Long userId) {
        return productReviewDao.getMyReviewsByUserId(userId);
    }
@Override
public void addReview(Long userId, Long orderId, Long orderItemId, Integer rating, String content, Integer serviceRating) {
    try {
        ProductReview review = new ProductReview();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setOrderItemId(orderItemId); // 关键
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedTime(new Date());
        review.setDeleted(0);
        // serviceRating 容错处理
        review.setServiceRating(serviceRating != null ? serviceRating : 5);
         // 关键：查出 productId 和 productName 并赋值
        OrderItem orderItem = orderItemDao.getById(orderItemId);
        if (orderItem == null) {
             throw new RuntimeException("订单项不存在，无法评论！");
        }
        review.setProductId(orderItem.getProductId());
        review.setProductName(orderItem.getProductName()); // 保存商品名称
        // 通过 orderId 查订单，获取 merchantId
        Order order = orderDao.getOrderByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在，无法评论！");
        }
        review.setMerchantId(order.getMerchantId());
        productReviewDao.insert(review);
    } catch (Exception e) {
        throw new RuntimeException("添加评论失败: " + e.getMessage(), e);
    }
}

    @Override
    public List<ProductReview> getReviewsByProductId(Long productId) {
        return productReviewDao.getReviewsByProductId(productId);
    }

    @Override
    public Double getAverageRatingByProductId(Long productId) {
        return productReviewDao.getAverageRatingByProductId(productId);
    }

    @Override
    public List<ProductReview> getReviewsByMerchantId(Long merchantId) {
        return productReviewDao.getReviewsByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void replyReview(Long reviewId, Long merchantId, String reply, Integer merchantRating) {
        if (reviewId == null) {
            throw new RuntimeException("评价ID不能为空");
        }
        if (merchantId == null) {
            throw new RuntimeException("商家ID不能为空");
        }
        if (reply == null || reply.trim().isEmpty()) {
            throw new RuntimeException("回复内容不能为空");
        }
        if (merchantRating == null || merchantRating < 1 || merchantRating > 5) {
            throw new RuntimeException("商家评分必须在1-5之间");
        }

        ProductReview review = productReviewDao.selectById(reviewId);
        if (review == null) {
            throw new RuntimeException("评价不存在");
        }
        if (!review.getMerchantId().equals(merchantId)) {
            throw new RuntimeException("无权限回复该评价");
        }
        if (review.getReply() != null) {
            throw new RuntimeException("该评价已回复");
        }

        try {
            int result = productReviewDao.updateReply(reviewId, reply, merchantRating);
            if (result <= 0) {
                throw new RuntimeException("回复失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("回复评价失败: " + e.getMessage());
        }
    }

    @Override
    public List<ProductReview> getReviewsByUserId(Long userId) {
        return productReviewDao.getReviewsByUserId(userId);
    }
}