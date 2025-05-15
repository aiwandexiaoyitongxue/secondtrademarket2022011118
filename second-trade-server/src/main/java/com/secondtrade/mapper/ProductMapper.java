package com.secondtrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.secondtrade.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
} 