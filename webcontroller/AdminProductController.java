package com.secondtrade.controller;

import com.secondtrade.common.Result;
import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    // 获取待审核商品列表
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Product>> getPendingProducts() {
        return Result.success(productService.getPendingProducts());
    }

    // 审核商品
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> auditProduct(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        System.out.println("审核接口被调用, id=" + id + ", req=" + req);
        try {
            boolean approved = (boolean) req.get("approved");
            String reason = (String) req.get("reason");
            productService.auditProduct(id, approved, reason);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }
}