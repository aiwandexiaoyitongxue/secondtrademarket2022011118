package com.secondtrade.webcontroller;

import com.secondtrade.common.Result;
import com.secondtrade.entity.User;
import com.secondtrade.service.UserService;
import com.secondtrade.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin // 允许跨域请求
public class UserController extends BaseController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, Object> registerData, HttpSession session) {
        try {
            // 1. 验证码校验
            String captcha = (String) registerData.get("captcha");
            String sessionCaptcha = (String) session.getAttribute("captcha");
            if (sessionCaptcha == null) {
                return Result.error("验证码已过期");
            }
            if (!captcha.equalsIgnoreCase(sessionCaptcha)) {
                return Result.error("验证码错误");
            }
            session.removeAttribute("captcha");

            // 2. 构造用户对象
            User user = new User();
            user.setUsername((String) registerData.get("username"));
            user.setPassword((String) registerData.get("password"));
            user.setPhone((String) registerData.get("phone"));
            user.setEmail((String) registerData.get("email"));
            user.setRealName((String) registerData.get("realName"));
            user.setCity((String) registerData.get("city"));
            
            // 处理gender字段
            Object genderObj = registerData.get("gender");
            if (genderObj != null) {
                if (genderObj instanceof Integer) {
                    user.setGender((Integer) genderObj);
                } else if (genderObj instanceof String) {
                    user.setGender(Integer.parseInt((String) genderObj));
                }
            }
            
            user.setBankAccount((String) registerData.get("bankAccount"));
            
            // 处理role字段
            String roleStr = (String) registerData.get("role");
            if (roleStr != null) {
                switch (roleStr) {
                    case "user":
                        user.setRole(0);
                        break;
                    case "merchant":
                        user.setRole(1);
                        break;
                    case "admin":
                        user.setRole(2);
                        break;
                    default:
                        user.setRole(0);
                }
            } else {
                user.setRole(0); // 默认普通用户
            }

            // 3. 参数校验
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
                return Result.error("手机号不能为空");
            }

            // 4. 执行注册
            userService.register(user);
            return Result.success(null);
            
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        try {
            String username = loginForm.get("username");
            String password = loginForm.get("password");

            // 1. 参数校验
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }

            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }

            // 2. 执行登录
            User user = userService.login(username, password);
            
            // 3. 清除敏感信息
            user.setPassword(null);
            
            // 4. 验证用户状态
            if (user.getStatus() != 1) {
                return Result.error("账号状态异常，请联系管理员");
            }
            
            // 5. 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId().intValue(), user.getRole());
            
            // 6. 构造返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("id", user.getId());
            resultMap.put("username", user.getUsername());
            resultMap.put("role", user.getRole());
            resultMap.put("token", token);
            
            return Result.success(resultMap);
            
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}