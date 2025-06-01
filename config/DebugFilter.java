package com.secondtrade.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(DebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");
        
        logger.info("收到请求: {} {} 认证头: {}", method, requestURI, authHeader != null ? "存在" : "不存在");
        
        // 请求通过过滤器链后，检查认证情况
        filterChain.doFilter(request, response);
        
        // 输出认证结果
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            logger.info("认证通过: 用户={}, 权限={}", authentication.getName(), authentication.getAuthorities());
        } else {
            logger.info("未认证请求");
        }
        
        logger.info("请求完成: {} {} 状态码: {}", method, requestURI, response.getStatus());
    }
} 