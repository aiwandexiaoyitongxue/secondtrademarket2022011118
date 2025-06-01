package com.secondtrade.webcontroller;

import com.secondtrade.entity.Merchant;
import com.secondtrade.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import com.secondtrade.common.exception.BusinessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import com.secondtrade.common.Result;  
import com.secondtrade.service.ProductService;
import com.secondtrade.entity.Product;
import com.secondtrade.entity.Category;
import com.secondtrade.service.CategoryService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
@Slf4j
@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    // 查询待审核商家
    @GetMapping("/pending")         
    @PreAuthorize("hasRole('ADMIN')")
    public List<Merchant> getPendingMerchants() {
        try {
            return merchantService.getPendingMerchants();
        } catch (Exception e) {
            log.error("获取待审核商家列表失败", e);
            throw new BusinessException("获取待审核商家列表失败");
        }
    }

    // 审核通过
    @PostMapping("/{id}/approve")           
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void approveMerchant(@PathVariable Long id) {
        merchantService.approveMerchant(id);
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void rejectMerchant(@PathVariable Long id) {
        merchantService.rejectMerchant(id);
    }
   @GetMapping("/info")
    public Result<Merchant> getMerchantInfo() {
        // 从 SecurityContext 获取当前登录用户
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return Result.error("未登录");
        }
        
        String username = ((UserDetails) principal).getUsername();
        // 通过用户名获取用户ID
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 通过用户ID获取商家信息
        Merchant merchant = merchantService.getMerchantByUserId(user.getId());
        if (merchant == null) {
            return Result.error("商家信息不存在");
        }
        
        return Result.success(merchant);
    }
    // 工具方法：获取当前登录用户id
    private Long getCurrentUserId() {
        // 你可以根据你的安全框架调整
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // 假设你的UserDetails里有getId方法
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.secondtrade.security.SecurityUser) {
            return ((com.secondtrade.security.SecurityUser) principal).getId();
        }
        return null;
    }
    // 禁用商家
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public void disableMerchant(@PathVariable Long id) {
        merchantService.disableMerchant(id);
    }

    // 查询所有商家
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<Merchant> getAllMerchants() {
        return merchantService.getAllMerchants();
    }

    // 查询单个商家
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public Merchant getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id);
    }

    // 商品分类统计接口
    @GetMapping("/product/category-stats")
    public Result<?> getCategoryStats(Authentication authentication) {
        // 获取当前登录用户
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return Result.error("未登录");
        }
        String username = ((UserDetails) principal).getUsername();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        Merchant merchant = merchantService.getMerchantByUserId(user.getId());
        if (merchant == null) {
            return Result.error("商家信息不存在");
        }
        // 查询该商家所有商品
        List<Product> products = productService.getProductList(null, null, merchant.getId(), null, null, 1, 10000);
        // 统计分类ID出现次数
        Map<Long, Integer> categoryCount = new HashMap<>();
        for (Product p : products) {
            if (p.getCategoryId() != null) {
                categoryCount.put(p.getCategoryId(), categoryCount.getOrDefault(p.getCategoryId(), 0) + 1);
            }
        }
        // 查询所有分类
        List<Category> allCategories = categoryService.list();
        // 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : categoryCount.entrySet()) {
            Long categoryId = entry.getKey();
            Integer count = entry.getValue();
            String name = allCategories.stream().filter(c -> c.getId().equals(categoryId)).map(Category::getName).findFirst().orElse("未知分类");
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("count", count);
            result.add(map);
        }
        // 按数量降序排序
        result.sort((a, b) -> ((Integer) b.get("count")).compareTo((Integer) a.get("count")));
        return Result.success(result);
    }
}