package com.secondtrade.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretString;

    private Key getSigningKey() {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                Claims claims = validateToken(token);
                if (claims != null) {
                    setUpSpringAuthentication(claims);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            logger.error("JWT认证失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Token验证失败", e);
            return null;
        }
    }

    private void setUpSpringAuthentication(Claims claims) {
        String username = claims.getSubject();
        
        // 从claims中获取角色信息
        Integer roleId = claims.get("role", Integer.class);
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色ID作为权限
        if (roleId != null) {
            // 添加原始的数字角色
            authorities.add(new SimpleGrantedAuthority(roleId.toString()));
            
            // 同时添加带ROLE_前缀的角色，以兼容hasRole方法
            switch (roleId) {
                case 0:
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                    break;
                case 1:
                    authorities.add(new SimpleGrantedAuthority("ROLE_MERCHANT"));
                    break;
                case 2:
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    break;
            }
        }
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
} 