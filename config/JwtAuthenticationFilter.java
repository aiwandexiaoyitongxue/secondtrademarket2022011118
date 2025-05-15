package com.secondtrade.config;

import com.secondtrade.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

   @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
    String token = request.getHeader("Authorization");
    System.out.println("收到token: " + token);
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);
        try {
            Claims claims = jwtUtil.parseToken(token);
            Integer role = claims.get("role", Integer.class);
            System.out.println("解析出的role: " + role);
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (role == 2) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    claims.get("username"),
                    null,
                    authorities
                );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("认证信息已设置: " + authentication);
        } catch (Exception e) {
            System.out.println("token解析失败: " + e.getMessage());
        }
    }
    chain.doFilter(request, response);
}
}