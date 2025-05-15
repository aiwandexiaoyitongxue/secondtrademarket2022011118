package com.secondtrade.service;

import com.secondtrade.entity.ProductImage;
import java.util.List;

public interface ProductImageService {
    List<ProductImage> findByProductId(Long productId);
    ProductImage saveImage(ProductImage productImage);
    void deleteByProductId(Long productId);
    boolean saveBatch(List<ProductImage> images);
    void removeByProductId(Long productId);
} 