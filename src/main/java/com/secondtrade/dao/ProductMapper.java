package com.secondtrade.dao;

import com.secondtrade.entity.Product;
import com.secondtrade.entity.User;
import com.secondtrade.entity.Merchant;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {
    // 查询所有商品（带主图）
    @Select("SELECT p.*, pi.url AS image FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
            "WHERE p.status=1 AND p.deleted=0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "condition", column = "condition"),
        @Result(property = "size", column = "size"),
        @Result(property = "isNegotiable", column = "is_negotiable"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "updatedTime", column = "updated_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "image", column = "image")
    })
    List<Product> selectAllWithImage();

    // 查询单个商品详情（带主图）
    @Select("SELECT p.*, pi.url AS image FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
            "WHERE p.id = #{id} AND p.status=1 AND p.deleted=0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "condition", column = "condition"),
        @Result(property = "size", column = "size"),
        @Result(property = "isNegotiable", column = "is_negotiable"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "updatedTime", column = "updated_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "image", column = "image")
    })
    Product selectByIdWithImage(Long id);

    // 查询所有商品（不带图片）
    @Select("SELECT * FROM product WHERE status=1 AND deleted=0")
    List<Product> selectAll();

    // 查询单个商品（不带图片）
    @Select("SELECT * FROM product WHERE id = #{id} AND status=1 AND deleted=0")
    Product selectById(Long id);

    // 查询待审核商品（带主图）
    @Select("SELECT p.*, pi.url AS image FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
            "WHERE p.status=0 AND p.deleted=0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "merchantId", column = "merchant_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "condition", column = "condition"),
        @Result(property = "size", column = "size"),
        @Result(property = "isNegotiable", column = "is_negotiable"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdTime", column = "created_time"),
        @Result(property = "updatedTime", column = "updated_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "image", column = "image")
    })
    List<Product> selectPendingProducts();

    // 审核通过商品
    @Update("UPDATE product SET status = 1 WHERE id = #{id}")
    int approveProduct(Long id);

    // 审核驳回商品
    @Update("UPDATE product SET status = 2 WHERE id = #{id}")
    int rejectProduct(Long id);
    // 插入商品
    @Insert("INSERT INTO product (merchant_id, category_id, name, description, original_price, price, stock, sales, condition, size, is_negotiable, status, created_time, updated_time, deleted) " +
            "VALUES (#{merchantId}, #{categoryId}, #{name}, #{description}, #{originalPrice}, #{price}, #{stock}, #{sales}, #{condition}, #{size}, #{isNegotiable}, #{status}, #{createdTime}, #{updatedTime}, #{deleted})")
    int insertProduct(Product product); 
    // 更新商品
    @Update("UPDATE product SET merchant_id=#{merchantId}, category_id=#{categoryId}, name=#{name}, description=#{description}, original_price=#{originalPrice}, price=#{price}, stock=#{stock}, sales=#{sales}, condition=#{condition}, size=#{size}, is_negotiable=#{isNegotiable}, status=#{status}, updated_time=#{updatedTime} WHERE id=#{id}")
    int updateProduct(Product product);
    // 删除商品
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteProduct(Long id);
 
}