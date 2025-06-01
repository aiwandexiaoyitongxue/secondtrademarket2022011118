package com.secondtrade.dao;

import com.secondtrade.dto.MerchantReviewDTO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.secondtrade.entity.MerchantReview;
@Mapper
public interface MerchantReviewDao {
   @Select("SELECT r.id, u.username as merchantName, o.order_no as orderNo, " +
        "r.rating, r.content, r.reply, r.created_time " +
        "FROM merchant_review r " +
        "LEFT JOIN merchant m ON r.merchant_id = m.id " +
        "LEFT JOIN user u ON m.user_id = u.id " +
        "LEFT JOIN product_order o ON r.order_id = o.id " +
        "WHERE r.user_id = #{userId} AND r.deleted = 0 " +
        "ORDER BY r.created_time DESC " +
        "LIMIT #{offset}, #{limit}")
List<MerchantReviewDTO> getMerchantCommentsByUserId(@Param("userId") Long userId,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM merchant_review WHERE user_id = #{userId} AND deleted = 0")
    int countMerchantCommentsByUserId(@Param("userId") Long userId);

    List<com.secondtrade.entity.MerchantReview> selectByMerchantId(@Param("merchantId") Long merchantId,
                                            @Param("hasReply") Boolean hasReply,
                                            @Param("offset") int offset,
                                            @Param("size") int size);

    int countByMerchantId(@Param("merchantId") Long merchantId,
                          @Param("hasReply") Boolean hasReply);

    Double getAverageRating(@Param("merchantId") Long merchantId);

    com.secondtrade.entity.MerchantReview selectById(@Param("id") Long id);

    int updateReply(@Param("id") Long id, @Param("reply") String reply);

    @Select("SELECT * FROM merchant_review WHERE order_id = #{orderId} AND user_id = #{userId} AND merchant_id = #{merchantId} AND deleted = 0 LIMIT 1")
    MerchantReview selectByOrderUserMerchant(@Param("orderId") Long orderId, @Param("userId") Long userId, @Param("merchantId") Long merchantId);

    @Update("UPDATE merchant_review SET reply = #{reply}, reply_time = #{replyTime} WHERE id = #{id}")
    int updateReplyById(@Param("id") Long id, @Param("reply") String reply, @Param("replyTime") java.time.LocalDateTime replyTime);
}