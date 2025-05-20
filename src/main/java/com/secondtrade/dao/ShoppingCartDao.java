package com.secondtrade.dao;

import com.secondtrade.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import java.util.List;
import com.secondtrade.dto.CartItemDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface ShoppingCartDao {
    // 查询当前用户购物车
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND deleted = 0")
    List<ShoppingCart> getCartByUserId(Long userId);

    // 更新购物车商品数量
    @Update("UPDATE shopping_cart SET quantity = #{quantity}, updated_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int updateQuantity(@Param("id") Long id, @Param("userId") Long userId, @Param("quantity") Integer quantity);

    // 删除购物车商品
    @Delete("DELETE FROM shopping_cart WHERE id = #{id} AND user_id = #{userId}")
    int deleteCartItem(@Param("id") Long id, @Param("userId") Long userId);

    // 添加商品到购物车（如果已存在可先查再增/改）
    @Insert("INSERT INTO shopping_cart (user_id, product_id, merchant_id, quantity, selected, created_time, updated_time, deleted) " +
            "VALUES (#{userId}, #{productId}, #{merchantId}, #{quantity}, #{selected}, NOW(), NOW(), 0)")
    int addCartItem(ShoppingCart cart);

    // 清空购物车
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int clearCart(Long userId);
    
    @Select("SELECT c.id, c.product_id, c.quantity, c.selected, p.name AS productName, p.price " +
        "FROM shopping_cart c " +
        "JOIN product p ON c.product_id = p.id " +
        "WHERE c.user_id = #{userId} AND c.deleted = 0")
    List<CartItemDTO> getCartItemDTOsByUserId(@Param("userId") Long userId);


}