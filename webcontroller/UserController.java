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
import com.secondtrade.dto.RegisterDTO;
import org.springframework.web.multipart.MultipartFile;
import com.secondtrade.service.MerchantReviewService;
import com.secondtrade.dto.MerchantReviewDTO;
import com.secondtrade.entity.WalletRecord;
import com.secondtrade.service.MerchantService;
import com.secondtrade.entity.Merchant;
import java.math.BigDecimal;
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 允许跨域请求
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil; // 注入 JwtUtil
    @Autowired
    private MerchantReviewService merchantReviewService; // 注入 MerchantReviewService
    @Autowired
    private MerchantService merchantService; // 注入 MerchantService
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. 参数校验
            if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "用户名不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "手机号不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 2. 注册用户
            userService.register(dto);
            
            // 3. 获取注册后的用户信息
            User user = userService.getUserByUsername(dto.getUsername());

            // 4. 如果是商家角色,创建商家记录
            if (dto.getRole() != null && dto.getRole() == User.ROLE_MERCHANT) {
                Merchant merchant = new Merchant();
                merchant.setUserId(user.getId());
                merchant.setName(dto.getUsername());
                merchant.setLevel(1);
                merchant.setStatus(0); // 设置为待审核状态
                merchant.setTotalSales(BigDecimal.ZERO);
                merchant.setSatisfactionRate(BigDecimal.valueOf(5.0));
                merchantService.createMerchant(merchant);
            }

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

            // 如果是商家，返回商家ID
            if (user.getRole() == User.ROLE_MERCHANT) {
                Merchant merchant = merchantService.getMerchantByUserId(user.getId());
                if (merchant != null) {
                    userInfo.put("merchantId", merchant.getId());
                    userInfo.put("merchantStatus", merchant.getStatus());
                }
            }

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
        userInfo.put("status", user.getStatus());
        userInfo.put("city", user.getCity());
        userInfo.put("gender", user.getGender());
        userInfo.put("description", user.getDescription());
        userInfo.put("wechat", user.getWechat());
        userInfo.put("avatar", user.getAvatar());
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
/**
     * 更新用户基本信息
     */
    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserInfo(Authentication authentication, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            user.setId(userId);
            
            // 只允许更新特定字段
            User existingUser = userService.getUserById(userId);
            existingUser.setRealName(user.getRealName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setCity(user.getCity());
            existingUser.setGender(user.getGender());
            existingUser.setDescription(user.getDescription());
            existingUser.setWechat(user.getWechat());
            
            userService.updateUser(existingUser);
            
            response.put("success", true);
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody Map<String, String> passwords) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            
            userService.updatePassword(userId, oldPassword, newPassword);
            
            response.put("success", true);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadAvatar(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            
            // 调用 service 层处理文件上传
            String avatarUrl = userService.uploadAvatar(userId, file);
            
            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("data", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户收到的商家评价列表
     */
    @GetMapping("/merchant-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserMerchantReviews(
            Authentication authentication,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = ((SecurityUser) authentication.getPrincipal()).getUser().getId();
            List<MerchantReviewDTO> reviews = merchantReviewService.getUserReceivedReviews(userId, page, size);
            int total = merchantReviewService.countUserReceivedReviews(userId);
            response.put("success", true);
            response.put("data", Map.of("records", reviews, "total", total));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取商家评价失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    
}