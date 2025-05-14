package com.secondtrade.webcontroller;

import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    @Autowired
    private UserService userService;

    // 获取待审核用户列表
    @GetMapping("/pending")
    public List<User> getPendingUsers() {
        return userService.getPendingUsers();
    }

    // 审核通过
    @PostMapping("/{id}/approve")
    public String approveUser(@PathVariable Long id) {
        userService.approveUser(id);
        return "success";
    }

    // 审核驳回
    @PostMapping("/{id}/reject")
    public String rejectUser(@PathVariable Long id) {
        userService.rejectUser(id);
        return "success";
    }

    // 禁用用户
    @PostMapping("/{id}/disable")
    public String disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return "success";
    }

    // 查询所有用户
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}