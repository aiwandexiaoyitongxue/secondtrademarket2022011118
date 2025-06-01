package com.secondtrade.dao;
import com.secondtrade.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.secondtrade.entity.OrderItem;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
import com.secondtrade.entity.Merchant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
@Mapper
public interface OrderDao {
    List<Order> selectUserOrders(
        @Param("userId") Long userId,
        @Param("orderNo") String orderNo,
        @Param("status") Integer status,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
   @Select("SELECT * FROM product_order WHERE id = #{orderId} AND user_id = #{userId} AND deleted = 0")
    Order getOrderByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);
   @Update("UPDATE product_order SET status = #{status}, receive_time = #{receiveTime} WHERE id = #{orderId} AND user_id = #{userId}")
    void updateStatusAndReceiveTime(
        @Param("orderId") Long orderId,
        @Param("status") Integer status,
        @Param("receiveTime") Date receiveTime,
        @Param("userId") Long userId // 补上这个
    );

    @Select("SELECT * FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND (#{status} IS NULL OR status = #{status}) " +
            "AND deleted = 0 ORDER BY created_time DESC LIMIT #{offset}, #{size}")
    List<Order> getMerchantOrders(
        @Param("merchantId") Long merchantId,
        @Param("status") Integer status,
        @Param("offset") int offset,
        @Param("size") int size
    );

    @Select("SELECT COUNT(*) FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND (#{status} IS NULL OR status = #{status}) AND deleted = 0")
    int countMerchantOrders(
        @Param("merchantId") Long merchantId,
        @Param("status") Integer status
    );

    @Select("SELECT COUNT(*) FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND DATE(created_time) = CURDATE() AND deleted = 0")
    int countTodayMerchantOrders(@Param("merchantId") Long merchantId);

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND DATE(created_time) = CURDATE() AND deleted = 0")
    double sumTodayMerchantOrders(@Param("merchantId") Long merchantId);

    @Select("SELECT COUNT(*) FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND DATE_FORMAT(created_time, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') AND deleted = 0")
    int countMonthMerchantOrders(@Param("merchantId") Long merchantId);

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND DATE_FORMAT(created_time, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') AND deleted = 0")
    double sumMonthMerchantOrders(@Param("merchantId") Long merchantId);

    @Select("SELECT * FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND status IN (3, 4) AND EXISTS (SELECT 1 FROM order_item WHERE order_id = product_order.id AND refund_status = 1) " +
            "AND deleted = 0 ORDER BY created_time DESC")
    List<Order> getMerchantRefundApplyOrders(@Param("merchantId") Long merchantId);

    @Select("SELECT * FROM product_order WHERE id = #{orderId} AND deleted = 0")
    Order getOrderById(@Param("orderId") Long orderId);

    @Update("UPDATE product_order SET status = #{status} WHERE id = #{orderId}")
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    @Update("UPDATE product_order SET refund_time = #{refundTime} WHERE id = #{orderId}")
    void updateRefundTime(@Param("orderId") Long orderId, @Param("refundTime") Date refundTime);

    @Update("UPDATE product_order SET refund_reason = #{reason} WHERE id = #{orderId}")
    void updateRefundReason(@Param("orderId") Long orderId, @Param("reason") String reason);

    @Select("SELECT * FROM product_order WHERE merchant_id = #{merchantId} " +
            "AND status = 6 AND deleted = 0 ORDER BY refund_time DESC")
    List<Order> getMerchantRefundedOrders(@Param("merchantId") Long merchantId);
    @Select("SELECT * FROM product_order WHERE id = #{orderId} AND merchant_id = #{merchantId} AND deleted = 0")
    Order getOrderByIdAndMerchantId(@Param("orderId") Long orderId, @Param("merchantId") Long merchantId);
     @Insert("INSERT INTO product_order (order_no, user_id, merchant_id, total_amount, pay_amount, " +
            "points_amount, points_used, status, created_time, updated_time, deleted, coupon_id, coupon_amount) " +
            "VALUES (#{orderNo}, #{userId}, #{merchantId}, #{totalAmount}, #{payAmount}, " +
            "#{pointsAmount}, #{pointsUsed}, #{status}, #{createdTime}, #{updatedTime}, #{deleted}, #{couponId}, #{couponAmount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    @Select("SELECT * FROM product_order WHERE order_no = #{orderNo} AND deleted = 0")
    Order getOrderByOrderNo(@Param("orderNo") String orderNo);
    @Select("SELECT * FROM product_order WHERE user_id = #{userId} " +
        "AND (#{status} IS NULL OR status = #{status}) " +
        "AND deleted = 0 ORDER BY created_time DESC LIMIT #{offset}, #{size}")
        List<Order> getUserOrders(
        @Param("userId") Long userId,
        @Param("status") Integer status,
        @Param("offset") int offset,
        @Param("size") int size
        );

        @Select("SELECT COUNT(*) FROM product_order WHERE user_id = #{userId} " +
                "AND (#{status} IS NULL OR status = #{status}) AND deleted = 0")
        int countUserOrders(
        @Param("userId") Long userId,
        @Param("status") Integer status
        );
        @Select("<script>" +
        "SELECT * FROM product_order WHERE user_id = #{userId} AND deleted = 0 " +
        "<if test='orderNo != null and orderNo != \"\"'>" +
        "   AND order_no LIKE CONCAT('%', #{orderNo}, '%') " +
        "</if>" +
        "<if test='status != null'>" +
        "   AND status = #{status} " +
        "</if>" +
        "<if test='startDate != null and startDate != \"\"'>" +
        "   AND DATE(created_time) &gt;= #{startDate} " +
        "</if>" +
        "<if test='endDate != null and endDate != \"\"'>" +
        "   AND DATE(created_time) &lt;= #{endDate} " +
        "</if>" +
        "ORDER BY created_time DESC" +
        "</script>")
        List<Order> searchOrders(
        @Param("userId") Long userId,
        @Param("orderNo") String orderNo,
        @Param("status") Integer status,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
        );
        @Select("SELECT COUNT(*) FROM product_order WHERE user_id = #{userId} " +
        "AND DATE(created_time) = CURDATE() AND deleted = 0")
        int countTodayUserOrders(@Param("userId") Long userId);

        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM product_order WHERE user_id = #{userId} " +
                "AND DATE(created_time) = CURDATE() AND deleted = 0")
        double sumTodayUserOrders(@Param("userId") Long userId);

        @Select("SELECT COUNT(*) FROM product_order WHERE user_id = #{userId} " +
                "AND DATE_FORMAT(created_time, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') AND deleted = 0")
        int countMonthUserOrders(@Param("userId") Long userId);

        @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM product_order WHERE user_id = #{userId} " +
                "AND DATE_FORMAT(created_time, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') AND deleted = 0")
        double sumMonthUserOrders(@Param("userId") Long userId);
}