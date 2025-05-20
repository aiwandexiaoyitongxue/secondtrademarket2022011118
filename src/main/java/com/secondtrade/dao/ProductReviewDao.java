package com.secondtrade.dao;

import com.secondtrade.dto.MyReviewDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import com.secondtrade.entity.ProductReview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
public interface ProductReviewDao {

 @Select("SELECT p.name as productName, r.rating, r.service_rating as serviceRating, r.content, r.created_time " +
        "FROM product_review r " +
        "LEFT JOIN product p ON r.product_id = p.id " +
        "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
        "ORDER BY r.created_time DESC")
List<MyReviewDTO> getMyReviewsByUserId(@Param("userId") Long userId);
        @Insert("INSERT INTO product_review (order_id, order_item_id, product_id, user_id, merchant_id, rating, service_rating, content, created_time, deleted) " +
        "VALUES (#{orderId}, #{orderItemId}, #{productId}, #{userId}, #{merchantId}, #{rating}, #{serviceRating}, #{content}, #{createdTime}, #{deleted})")
        void insert(ProductReview review);

}