package com.secondtrade.controller;

import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import com.secondtrade.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users") 
@PreAuthorize("hasRole('ADMIN')") // 添加权限注解   
public class AdminUserController {
    @Autowired
    private UserService userService;

    // 获取待审核用户列表
    @GetMapping("/pending")  
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<User> getPendingUsers() {
        return userService.getPendingUsers();
    }

    // 审核通过
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String approveUser(@PathVariable Long id) {
        userService.approveUser(id);
        return "success";
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public String rejectUser(@PathVariable Long id) {
        userService.rejectUser(id);
        return "success";
    }

    // 禁用用户
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public Result<Void> disableUser(@PathVariable Long id) {
        try {
            userService.disableUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 查询所有用户
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // 添加权限注解
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 更新用户信息
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return "success";
    }
}