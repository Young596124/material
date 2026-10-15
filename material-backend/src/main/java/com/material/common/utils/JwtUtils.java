package com.material.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Map;

/**
 * Jwt工具类
 */
@Component
public class JwtUtils
{
    /** 过期时间（默认30分钟） */
    public static long EXPIRE;

    /** 密钥 */
    public static String SECRET;

    private static SecretKey key;


    /** 注入密钥 —— @Value 必须标在非静态 setter 上 */
    @Value("${token.secret}")
    public void setSecret(String secret)
    {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /** 注入过期时间（分钟 → 毫秒） */
    @Value("${token.expireTime:30}")
    public void setExpireTime(long minutes)
    {
        EXPIRE = minutes * 60 * 1000L;
    }

    /**
     * 生成token
     */
    public static String createToken(Map<String, Object> claims)
    {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(key)
                .compact();
    }

    /**
     * 解析token
     */
    public static Claims parseToken(String token)
    {
        try
        {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 是否过期
     */
    public static boolean isExpired(String token)
    {
        try
        {
            Claims claims = parseToken(token);
            return claims == null || claims.getExpiration().before(new Date());
        }
        catch (Exception e)
        {
            return true;
        }
    }
}
