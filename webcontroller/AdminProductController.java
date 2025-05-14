package com.secondtrade.controller;

import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    // 获取待审核商品列表
    @GetMapping("/pending")
    public List<Product> getPendingProducts() {
        return productService.getPendingProducts();
    }

    // 审核通过
    @PostMapping("/{id}/approve")
    public String approveProduct(@PathVariable Long id) {
        productService.approveProduct(id);
        return "success";
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    public String rejectProduct(@PathVariable Long id) {
        productService.rejectProduct(id);
        return "success";
    }
}