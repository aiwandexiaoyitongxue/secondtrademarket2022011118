package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import java.util.List;
import com.secondtrade.dto.CartItemDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
    // 查询当前用户购物车
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND deleted = 0")
    List<ShoppingCart> getCartByUserId(Long userId);

    // 更新购物车商品数量
    @Update("UPDATE shopping_cart SET quantity = #{quantity}, updated_time = NOW() WHERE id = #{id} AND user_id = #{userId} AND deleted = 0")
    int updateQuantity(@Param("id") Long id, @Param("userId") Long userId, @Param("quantity") Integer quantity);

    // 删除购物车商品
    @Delete("UPDATE shopping_cart SET deleted = 1, updated_time = NOW() WHERE id = #{id} AND user_id = #{userId}")
    int deleteCartItem(@Param("id") Long id, @Param("userId") Long userId);

    // 添加商品到购物车
    @Insert("INSERT INTO shopping_cart (user_id, product_id, merchant_id, quantity, selected, created_time, updated_time, deleted) " +
            "VALUES (#{userId}, #{productId}, #{merchantId}, #{quantity}, #{selected}, NOW(), NOW(), 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addCartItem(ShoppingCart cart);

    // 清空购物车
    @Update("UPDATE shopping_cart SET deleted = 1, updated_time = NOW() WHERE user_id = #{userId}")
    int clearCart(Long userId);
    
    @Select("SELECT c.id, c.product_id, c.quantity, c.selected, p.name AS productName, CAST(p.price AS DOUBLE) as price, p.stock, " +
        "COALESCE(pi.url, '') as productImage " +
        "FROM shopping_cart c " +
        "JOIN product p ON c.product_id = p.id " +
        "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main = 1 AND pi.deleted = 0 " +
        "WHERE c.user_id = #{userId} AND c.deleted = 0")
    List<CartItemDTO> getCartItemDTOsByUserId(@Param("userId") Long userId);

    // 根据用户ID和商品ID查询购物车项
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND product_id = #{productId} LIMIT 1")
    ShoppingCart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    // 根据用户ID和商品ID更新购物车数量（仅更新未删除的记录）
    @Update("UPDATE shopping_cart SET quantity = #{quantity}, updated_time = NOW() " +
            "WHERE user_id = #{userId} AND product_id = #{productId} AND deleted = 0")
    int updateQuantityByUserIdAndProductId(@Param("userId") Long userId, 
                                         @Param("productId") Long productId, 
                                         @Param("quantity") Integer quantity);

    @Update("UPDATE shopping_cart SET quantity = #{quantity}, selected = #{selected}, deleted = #{deleted}, updated_time = #{updatedTime} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updateCartItem(ShoppingCart cart);

    // 新增方法：根据用户ID和产品ID列表删除购物车商品
    @Update("<script> " +
            "UPDATE shopping_cart SET deleted = 1, updated_time = NOW() " +
            "WHERE user_id = #{userId} AND product_id IN " +
            "<foreach item='productId' collection='productIds' open='(' separator=',' close=')'>" +
            "#{productId}" +
            "</foreach>" +
            "</script>")
    int deleteByUserIdAndProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);
}