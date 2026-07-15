package com.morbid.lingtuagent.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    // 从配置文件读取 JWT 密钥和过期时间
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;   // 单位：毫秒

    /*
     生成 JWT Token
     @param userId   用户ID
     @param username 用户名
     @return JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .subject(username)                    // 新 API：.subject()
                .claims(claims)                       // 自定义载荷
                .issuedAt(new Date())                 // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())            // 签名密钥
                .compact();
    }
    //解析 Token 并返回 Claims（验证签名和过期）
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())          // 新 API：.verifyWith()
                .build()
                .parseSignedClaims(token)             // 新 API：.parseSignedClaims()
                .getPayload();
    }
    //从 Token 提取用户ID
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    //从 Token 提取用户名
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    //判断 Token 是否过期
    public Boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration.before(new Date());
    }
    //验证 Token 是否有效（可选）
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
    // 生成签名密钥（基于配置的 secret 字符串）
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
