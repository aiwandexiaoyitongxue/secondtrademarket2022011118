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
        review.setServiceRating(serviceRating);
         // 关键：查出 productId 并赋值
        OrderItem orderItem = orderItemDao.getById(orderItemId);
        review.setProductId(orderItem.getProductId());
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
}