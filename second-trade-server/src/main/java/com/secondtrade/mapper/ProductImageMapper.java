package com.secondtrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    /**
     * 批量插入商品图片
     * @param images 图片列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ProductImage> images);
} 