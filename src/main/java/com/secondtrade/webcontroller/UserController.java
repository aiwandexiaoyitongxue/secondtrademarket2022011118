package com.secondtrade.webcontroller;

import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.HashMap;
import java.util.Map;
import com.secondtrade.util.JwtUtil;
import com.secondtrade.entity.Order;
import com.secondtrade.entity.UserAddress;  
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.secondtrade.security.SecurityUser;

@RestController
@RequestMapping("/api/user")
@CrossOrigin // 允许跨域请求
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. 参数校验
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "手机号不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 2. 执行注册
            userService.register(user);
            
            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginForm) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = loginForm.get("username");
            String password = loginForm.get("password");
           

            // 1. 参数校验
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 2. 执行登录
            User user = userService.login(username, password);
               // 生成 token
            String token = jwtUtil.generateToken(user); // JWT 工具类
        
            // 3. 返回用户信息（不包含敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());
            userInfo.put("token", token); // 返回 token
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", userInfo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
     /**
     * 获取用户信息
     */
  @GetMapping("/info")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserInfo(Authentication authentication) {
    Map<String, Object> response = new HashMap<>();
    try {
        System.out.println("当前认证信息: " + authentication);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "用户未认证");
            return ResponseEntity.status(401).body(response);
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        System.out.println("SecurityUser: " + securityUser);
        
        Long userId = securityUser.getUser().getId();
        System.out.println("用户ID: " + userId);
        
        User user = userService.getUserById(userId);
        System.out.println("查询到的用户: " + user);

        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus());  // 添加 status 字段
        response.put("success", true);
        response.put("data", userInfo);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        e.printStackTrace();
        response.put("success", false);
        response.put("message", "获取用户信息失败: " + e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
    /**
     * 获取用户钱包信息
     */
    @GetMapping("/wallet")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserWallet(Authentication authentication) {
    Map<String, Object> response = new HashMap<>();
    try {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        User wallet = userService.getUserWallet(userId);

        Map<String, Object> walletInfo = new HashMap<>();
        walletInfo.put("balance", wallet.getWalletBalance());
        walletInfo.put("points", wallet.getPoints());

        response.put("success", true);
        response.put("data", walletInfo);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

    /**
     * 获取用户订单列表
     */
   @GetMapping("/orders")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserOrders(Authentication authentication, @RequestParam(required = false) String status) {
    Map<String, Object> response = new HashMap<>();
    try {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<Order> orders;
        if (status != null && !status.equals("all")) {
            orders = userService.getUserOrdersByStatus(userId, status);
        } else {
            orders = userService.getUserOrders(userId);
        }
        response.put("success", true);
        response.put("data", orders);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

    /**
     * 获取用户地址列表
     */
   @GetMapping("/addresses")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getUserAddresses(Authentication authentication) {
    Map<String, Object> response = new HashMap<>();
    try {
        Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
        List<UserAddress> addresses = userService.getUserAddresses(userId);
        response.put("success", true);
        response.put("data", addresses);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

@PostMapping("/address/add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> addAddress(Authentication authentication, @RequestBody UserAddress address) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    address.setUserId(userId);
    userService.addUserAddress(address);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "新增成功");
    return ResponseEntity.ok(response);
}
@PostMapping("/address/update")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> updateAddress(Authentication authentication, @RequestBody UserAddress address) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    address.setUserId(userId);
    userService.updateUserAddress(address);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "修改成功");
    return ResponseEntity.ok(response);
}
@PostMapping("/address/delete")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> deleteAddress(Authentication authentication, @RequestBody Map<String, Long> body) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    Long addressId = body.get("id");
    userService.deleteUserAddress(userId, addressId);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "删除成功");
    return ResponseEntity.ok(response);
}
@PostMapping("/address/setDefault")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> setDefaultAddress(Authentication authentication, @RequestBody Map<String, Long> body) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    Long addressId = body.get("id");
    userService.setDefaultUserAddress(userId, addressId);
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "设置成功");
    return ResponseEntity.ok(response);
}
@GetMapping("/points")
public ResponseEntity<?> getPoints(Authentication authentication) {
    Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
    int points = userService.getUserPoints(userId);
    return ResponseEntity.ok(Map.of("success", true, "points", points));
}

}
