package com.secondtrade.service;

import com.secondtrade.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    List<Product> getPendingProducts();
    void approveProduct(Long id);
    void rejectProduct(Long id);
    Product addProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Long id);
}