package com.secondtrade.dao;

import com.secondtrade.dto.MyReviewDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import com.secondtrade.entity.ProductReview;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductReviewDao {

 @Select("SELECT r.id, r.product_id, r.order_item_id, r.product_name, r.rating, r.service_rating, r.content, r.created_time " +
        "FROM product_review r " +
        "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
        "ORDER BY r.created_time DESC")
List<MyReviewDTO> getMyReviewsByUserId(@Param("userId") Long userId);
        @Insert("INSERT INTO product_review (order_id, order_item_id, product_id, user_id, merchant_id, product_name, rating, service_rating, content, created_time, deleted) " +
        "VALUES (#{orderId}, #{orderItemId}, #{productId}, #{userId}, #{merchantId}, #{productName}, #{rating}, #{serviceRating}, #{content}, #{createdTime}, #{deleted})")
        void insert(ProductReview review);
        @Select("SELECT r.*, u.username " +
                "FROM product_review r " +
                "LEFT JOIN user u ON r.user_id = u.id " +
                "WHERE r.product_id = #{productId} AND r.deleted = 0 " +
                "ORDER BY r.created_time DESC")
        List<ProductReview> getReviewsByProductId(@Param("productId") Long productId);

    /**
     * 计算商品的平均评分
     */
    @Select("SELECT AVG(rating) FROM product_review WHERE product_id = #{productId} AND deleted = 0")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Select("SELECT r.*, u.username FROM product_review r LEFT JOIN user u ON r.user_id = u.id WHERE r.merchant_id = #{merchantId} AND r.deleted = 0 ORDER BY r.created_time DESC")
    List<ProductReview> getReviewsByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 根据ID查询评价
     * @param id 评价ID
     * @return 评价信息
     */
    @Select("SELECT * FROM product_review WHERE id = #{id} AND deleted = 0")
    ProductReview selectById(@Param("id") Long id);

    /**
     * 更新评价回复
     * @param id 评价ID
     * @param reply 回复内容
     * @param merchantRating 商家对用户的评分
     * @return 影响行数
     */
    @Update("UPDATE product_review SET reply = #{reply}, reply_time = NOW(), merchant_rating = #{merchantRating} WHERE id = #{id} AND deleted = 0")
    int updateReply(@Param("id") Long id, @Param("reply") String reply, @Param("merchantRating") Integer merchantRating);

    /**
     * 根据用户ID查询商品评价
     */
    @Select("SELECT * FROM product_review WHERE user_id = #{userId} AND deleted = 0 ORDER BY created_time DESC")
    List<ProductReview> getReviewsByUserId(@Param("userId") Long userId);
}