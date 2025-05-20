package com.secondtrade.controller;

import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解       
    // 获取待审核商品列表
    @GetMapping("/pending")
    public List<Product> getPendingProducts() {
        return productService.getPendingProducts();
    }

    // 审核通过
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
        return "success";
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String rejectProduct(@PathVariable Long id) {
        productService.rejectProduct(id);
        return "success";
    }
}