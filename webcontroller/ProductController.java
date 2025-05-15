package com.secondtrade.controller;

import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 商品列表
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解   
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 商品详情
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}