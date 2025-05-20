package com.secondtrade.service.impl;

import com.secondtrade.entity.Product;
import com.secondtrade.dao.ProductMapper;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

   @Override
public List<Product> getAllProducts() {
    // return productMapper.selectAll();
    return productMapper.selectAllWithImage(); // 用带图片的
}

@Override
public Product getProductById(Long id) {
    // return productMapper.selectById(id);
    return productMapper.selectByIdWithImage(id); // 用带图片的
}
// 新增实现
    @Override
    public List<Product> getPendingProducts() {
        return productMapper.selectPendingProducts();
    }

    @Override
    public void approveProduct(Long id) {
        productMapper.approveProduct(id);
    }

    @Override
    public void rejectProduct(Long id) {
        productMapper.rejectProduct(id);
    }
    @Override
    public Product addProduct(Product product) {
        productMapper.insertProduct(product); // 改这里
        return product;
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateProduct(product); // 改这里
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteProduct(id); // 改这里
    }
}