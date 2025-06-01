package com.secondtrade.dao;

import com.secondtrade.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import java.util.List;
import java.util.Date;
@Mapper
public interface OrderItemDao {
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId} AND deleted = 0")
    List<OrderItem> getItemsByOrderId(@Param("orderId") Long orderId);
    @Select("SELECT * FROM order_item WHERE id = #{id} AND deleted = 0")
    OrderItem getById(@Param("id") Long id);
    // 新增方法
    @Select("SELECT * FROM order_item WHERE id = #{id} AND deleted = 0")
    OrderItem getOrderItemById(@Param("id") Long id);

    @Update("UPDATE order_item SET refund_status = #{refundStatus} WHERE id = #{id}")
    void updateRefundStatus(@Param("id") Long id, @Param("refundStatus") Integer refundStatus);
    @Insert("INSERT INTO order_item (order_id, product_id, merchant_id, product_name, " +
            "product_image, price, quantity, total_amount, created_time, updated_time, deleted) " +
            "VALUES (#{orderId}, #{productId}, #{merchantId}, #{productName}, " +
            "#{productImage}, #{price}, #{quantity}, #{totalAmount}, #{createdTime}, " +
            "#{updatedTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);
}