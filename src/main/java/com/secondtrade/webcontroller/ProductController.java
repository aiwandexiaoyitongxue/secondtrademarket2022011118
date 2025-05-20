package com.secondtrade.controller;

import com.secondtrade.entity.Product;
import com.secondtrade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.secondtrade.security.SecurityUser;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
  
    // 1. 商品列表（所有人可访问）
    @GetMapping
    public Map<String, Object> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        Map<String, Object> result = new HashMap<>();
        result.put("records", products);
        result.put("total", products.size());
        return result;
    }

    // 2. 商品详情（所有人可访问）
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // 3. 商家发布商品（只有商家能发布）
    @PostMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Product addProduct(@RequestBody Product product, Authentication authentication) {
        Long currentUserId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        product.setMerchantId(currentUserId); // 或 setUserId
        return productService.addProduct(product);
    }

    // 4. 商家修改商品（只能修改自己发布的商品）
     @PutMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product, Authentication authentication) {
        Long currentUserId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        Product dbProduct = productService.getProductById(id);
        if (dbProduct == null) {
            return ResponseEntity.notFound().build();
        }
        if (!dbProduct.getMerchantId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只能操作自己发布的商品！");
        }
        product.setId(id);
        product.setMerchantId(currentUserId); // 防止被篡改
        productService.updateProduct(product);
        return ResponseEntity.ok("修改成功");
    }

    // 5. 商家/管理员删除商品（商家只能删自己商品，管理员可删任意商品）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MERCHANT') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        Product dbProduct = productService.getProductById(id);
        if (dbProduct == null) {
            return ResponseEntity.notFound().build();
        }
        Long currentUserId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !dbProduct.getMerchantId().equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只能删除自己发布的商品！");
        }
        productService.deleteProduct(id);
        return ResponseEntity.ok("删除成功");
    }

    // 6. 管理员审核商品（示例）
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> auditProduct(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        // 审核逻辑
        // req 里可以有审核状态、审核意见等
        // productService.auditProduct(id, req);
        return ResponseEntity.ok("审核成功");
    }
}