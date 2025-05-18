package com.secondtrade.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.secondtrade.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    void publishProduct(String productJson, List<MultipartFile> images) throws JsonProcessingException;
    
    List<Product> getProductList(Long categoryId, String keyword, Long merchantId, Integer status, Integer excludeStatus, Integer page, Integer size);
    
    Product getProductDetail(Long id);
    
    void updateProduct(Long id, Product product);
    
    void deleteProduct(Long id);
} 