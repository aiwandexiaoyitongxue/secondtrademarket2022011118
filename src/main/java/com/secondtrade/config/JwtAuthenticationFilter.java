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
        if (request.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        System.out.println("收到token: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Claims claims = jwtUtil.parseToken(token);
                Integer role = claims.get("role", Integer.class);
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
            } catch (Exception e) {
                System.out.println("token解析失败: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的token");
                return;
            }
        }
        chain.doFilter(request, response);
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