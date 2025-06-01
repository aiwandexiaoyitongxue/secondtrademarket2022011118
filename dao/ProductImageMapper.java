package com.secondtrade.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    /**
     * 批量插入商品图片
     * @param images 图片列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ProductImage> images);

    /**
     * 查询商品的所有图片
     * @param productId 商品ID
     * @return 图片列表
     */
    @Select("SELECT * FROM product_image WHERE product_id = #{productId} AND deleted = 0 ORDER BY sort ASC")
    List<ProductImage> selectByProductId(@Param("productId") Long productId);

    /**
     * 查询商品的主图
     * @param productId 商品ID
     * @return 主图信息
     */
    @Select("SELECT * FROM product_image WHERE product_id = #{productId} AND is_main = 1 AND deleted = 0 LIMIT 1")
    ProductImage selectMainImage(@Param("productId") Long productId);
} 