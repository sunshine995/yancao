package com.office.yancao.untils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成、解析、校验 JWT Token
 */
@Component
public class JwtUtil {

    // 从 application.yml 读取密钥和过期时间
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // 单位：秒

    private Key getSigningKey() {
        // 将 secret 字符串转换为适合 HS512 的密钥
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 JWT Token
     * @param userId 用户ID（作为 subject）
     * @return token 字符串
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration * 1000); // 转为毫秒

        return Jwts.builder()
                .setSubject(userId)                    // 主题：用户ID
                .setIssuedAt(now)                      // 签发时间
                .setExpiration(expireDate)            // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 签名算法和密钥
                .compact();
    }

    /**
     * 从 Token 中解析出 Claims（包含 subject、过期时间等）
     * @param token Token 字符串
     * @return Claims 对象
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())       // 指定签名密钥
                .build()
                .parseClaimsJws(token)                // 解析 Token
                .getBody();                           // 获取 payload 数据
    }

    /**
     * 从 Token 中提取用户ID（subject）
     * @param token Token 字符串
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 校验 Token 是否有效（未过期、签名正确）
     * @param token Token 字符串
     * @return true=有效，false=无效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            System.err.println("Token 签名无效: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token 格式错误: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token 已过期: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token 不支持: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Token 为空或非法: " + e.getMessage());
        }
        return false;
    }
}