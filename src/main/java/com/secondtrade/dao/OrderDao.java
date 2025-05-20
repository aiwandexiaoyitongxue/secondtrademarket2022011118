package com.secondtrade.dao;
import com.secondtrade.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.secondtrade.entity.OrderItem;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Date;
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
}