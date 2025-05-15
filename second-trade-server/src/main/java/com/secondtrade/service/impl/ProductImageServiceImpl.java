package com.secondtrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.secondtrade.entity.ProductImage;
import com.secondtrade.mapper.ProductImageMapper;
import com.secondtrade.service.ProductImageService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductImageServiceImpl extends ServiceImpl<ProductImageMapper, ProductImage> implements ProductImageService {
    
    @Override
    public List<ProductImage> findByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        return list(wrapper);
    }

    @Override
    public ProductImage saveImage(ProductImage productImage) {
        baseMapper.insert(productImage);
        return productImage;
    }

    @Override
    public void deleteByProductId(Long productId) {
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductImage::getProductId, productId);
        remove(wrapper);
    }

    @Override
    public boolean saveBatch(List<ProductImage> images) {
        if (images == null || images.isEmpty()) {
            return true;
        }
        return baseMapper.insertBatch(images) > 0;
    }

    @Override
    public void removeByProductId(Long productId) {
        deleteByProductId(productId);
    }
} 