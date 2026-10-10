package com.material.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import java.util.Date;
import java.util.Map;

/**
 * Jwt工具类
 */
public class JwtUtils
{
    /** 过期时间（默认30分钟） */
    public static final long EXPIRE = 30 * 60 * 1000L;

    /** 密钥 */
    public static final String SECRET = "abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789";

    private static SecretKey key;

    static
    {
        key = Keys.hmacShaKeyFor(SECRET.getBytes());
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
