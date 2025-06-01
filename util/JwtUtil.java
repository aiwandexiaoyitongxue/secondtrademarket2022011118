package com.secondtrade.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.secondtrade.entity.User;

@Component
public class JwtUtil {
    // 固定密钥，长度至少32位
    private static final String SECRET = "secondtrade-very-secret-key-2024-abc";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Long getUserIdFromToken(String token) {
    Claims claims = parseToken(token);
    // 你的token里存的是 "id"，类型是Long
    Object idObj = claims.get("id");
    if (idObj instanceof Integer) {
        return ((Integer) idObj).longValue();
    } else if (idObj instanceof Long) {
        return (Long) idObj;
    } else if (idObj instanceof String) {
        return Long.valueOf((String) idObj);
    }
    return null;
}
}