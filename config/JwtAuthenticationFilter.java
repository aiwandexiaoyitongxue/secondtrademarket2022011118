package com.secondtrade.config;

import com.secondtrade.entity.User;
import com.secondtrade.security.SecurityUser;
import com.secondtrade.service.UserService;
import com.secondtrade.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 处理 OPTIONS 请求
        String path = request.getRequestURI();
        System.out.println("JwtAuthenticationFilter处理请求: " + path);  // 添加调试日志

        if (
            path.equals("/api/merchant/ping") ||
            path.startsWith("/api/auth/") ||
            path.equals("/api/user/register") ||
            path.equals("/api/user/login") ||
            path.startsWith("/api/products") ||
            path.startsWith("/api/products/") ||
            path.startsWith("/css/") ||
            path.startsWith("/js/") ||
            path.startsWith("/images/") ||
            path.startsWith("/static/") ||
            path.startsWith("/static/upload/") ||
            path.startsWith("/static/avatar/") ||
            path.startsWith("/allimage/") ||
            path.startsWith("/allimage/publishimage/") ||
            path.startsWith("/api/allimage/") ||
            path.startsWith("/api/allimage/publishimage/") ||
            path.equals("/upload/public")
        ) {
            System.out.println("跳过JWT验证的路径: " + path);  // 添加调试日志
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        System.out.println("收到token: " + token);

        if (token == null || !token.startsWith("Bearer ")) {
            System.out.println("Token无效或不存在");  // 添加调试日志
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未授权");
            return;
        }

            token = token.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                Object roleObj = claims.get("role");
                Integer role;
                
                // 处理role值的转换
                if (roleObj instanceof String) {
                    String roleStr = (String) roleObj;
                    if (roleStr.startsWith("ROLE_")) {
                        roleStr = roleStr.substring(5);
                    }
                    role = Integer.parseInt(roleStr);
                } else if (roleObj instanceof Integer) {
                    role = (Integer) roleObj;
                } else if (roleObj instanceof Long) {
                    role = ((Long) roleObj).intValue();
                } else {
                    throw new IllegalArgumentException("Invalid role type in token");
                }
                
                String username = (String) claims.get("username");
                System.out.println("解析出的role: " + role);
                System.out.println("解析出的username: " + username);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role == 2) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else if (role == 1) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_MERCHANT"));
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }

                User user = userService.getUserByUsername(username);
                if (user == null) {
                System.out.println("用户不存在: " + username);  // 添加调试日志
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
                    return;
                }

                SecurityUser securityUser = new SecurityUser(user);
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        securityUser,
                        null,
                        authorities
                    );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("认证信息已设置: " + authentication);
            chain.doFilter(request, response);
            } catch (Exception e) {
                System.out.println("token解析失败: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的token");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}