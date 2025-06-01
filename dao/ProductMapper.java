package com.secondtrade.dao;

import com.secondtrade.entity.Product;
import com.secondtrade.entity.User;
import com.secondtrade.entity.Merchant;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 查询商品列表（带主图）
    @Select("<script>" +
        "SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, pi.url as image " +
        "FROM product p " +
        "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
        "WHERE p.deleted=0 AND p.status=1 " +
        "<if test='categoryId != null'> AND p.category_id = #{categoryId}</if> " +
        "<if test='keyword != null and keyword != \"\"'> AND p.name LIKE CONCAT('%', #{keyword}, '%')</if> " +
        "<if test='merchantId != null'> AND p.merchant_id = #{merchantId}</if> " +
        "<if test='status != null'> AND p.status = #{status}</if> " +
        "<if test='excludeStatus != null'> AND p.status != #{excludeStatus}</if> " +
        "ORDER BY p.created_time DESC " +
        "LIMIT #{size} OFFSET #{offset}" +
        "</script>")
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
    List<Product> selectProductListWithImage(@Param("categoryId") Long categoryId,
                                           @Param("keyword") String keyword,
                                           @Param("merchantId") Long merchantId,
                                           @Param("status") Integer status,
                                           @Param("excludeStatus") Integer excludeStatus,
                                           @Param("size") Integer size,
                                           @Param("offset") Integer offset);

    // 查询单个商品详情（带主图）
    @Select("SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, pi.url AS image FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
            "WHERE p.id = #{id} AND p.deleted=0")
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

    // 查询待审核商品（带主图）
    @Select("SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, pi.url AS image FROM product p " +
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
    @Update("UPDATE product SET status = 4 WHERE id = #{id}")
    int rejectProduct(Long id);

    // 查询所有商品（带主图）
    @Select("SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, pi.url AS image FROM product p " +
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

    // 查询所有商品（不带图片）
    @Select("SELECT * FROM product WHERE status=1 AND deleted=0")
    List<Product> selectAll();

    // 查询单个商品（不带图片）
    @Select("SELECT * FROM product WHERE id = #{id} AND status=1 AND deleted=0")
    Product selectById(Long id);

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

    // 查询商家商品（带主图）
    @Select("SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, pi.url AS image FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id AND pi.is_main=1 AND pi.deleted=0 " +
            "WHERE p.merchant_id = #{merchantId} AND p.deleted=0 " +
            "AND (#{status} IS NULL OR p.status = #{status}) " +
            "AND (#{excludeStatus} IS NULL OR p.status != #{excludeStatus})")
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
    List<Product> selectByMerchantIdWithImage(@Param("merchantId") Long merchantId, 
                                            @Param("status") Integer status,
                                            @Param("excludeStatus") Integer excludeStatus);

    @Select("<script>" +
            "SELECT p.id, p.merchant_id, p.category_id, p.name, p.description, p.original_price, p.price, p.stock, p.sales, p.condition, p.size, p.is_negotiable, p.status, p.created_time, p.updated_time, p.deleted, GROUP_CONCAT(pi.url) as image_urls " +
            "FROM product p " +
            "LEFT JOIN product_image pi ON p.id = pi.product_id " +
            "WHERE p.deleted = 0 " +
            "<if test='categoryId != null'> AND p.category_id = #{categoryId} </if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (p.name LIKE CONCAT('%', #{keyword}, '%') OR p.description LIKE CONCAT('%', #{keyword}, '%')) </if>" +
            "<if test='merchantId != null'> AND p.merchant_id = #{merchantId} </if>" +
            "<if test='status != null'> AND p.status = #{status} </if>" +
            "<if test='excludeStatus != null'> AND p.status != #{excludeStatus} </if>" +
            "GROUP BY p.id " +
            "ORDER BY p.created_time DESC" +
            "</script>")
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
        @Result(property = "image", column = "image_urls")
    })
    List<Product> selectProductsWithFilters(@Param("categoryId") Long categoryId,
                                          @Param("keyword") String keyword,
                                          @Param("merchantId") Long merchantId,
                                          @Param("status") Integer status,
                                          @Param("excludeStatus") Integer excludeStatus);

    @Select("SELECT c.name, COUNT(p.id) as count " +
            "FROM product p " +
            "JOIN category c ON p.category_id = c.id " +
            "WHERE p.merchant_id = #{merchantId} " +
            "AND p.deleted = 0 " +
            "AND p.status = 1 " +
            "GROUP BY c.id, c.name " +
            "ORDER BY count DESC")
    List<Map<String, Object>> selectCategoryStatsByMerchantId(@Param("merchantId") Long merchantId);

    @Select("SELECT IFNULL(SUM(IFNULL(sales,0) * IFNULL(price,0)), 0) FROM product WHERE merchant_id = #{merchantId} AND deleted = 0")
    Double getTotalSalesByMerchantId(@Param("merchantId") Long merchantId);
}