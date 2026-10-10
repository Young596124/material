package com.material.framework.manager;

import com.material.common.constant.Constants;
import com.material.common.utils.JwtUtils;
import com.material.common.utils.ServletUtils;
import com.material.modules.system.domain.LoginUser;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token验证处理
 */
@Component
public class TokenService
{
    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    /**
     * 创建令牌
     */
    public String createToken(LoginUser loginUser)
    {
        String tokenKey = java.util.UUID.randomUUID().toString();
        loginUser.setToken(tokenKey);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + JwtUtils.EXPIRE);

        // 存储到Redis
        String redisKey = getRedisKey(tokenKey);
        redisTemplate.opsForValue().set(redisKey, loginUser, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);

        // 生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, tokenKey);
        return JwtUtils.createToken(claims);
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser(HttpServletRequest request)
    {
        String token = getToken(request);
        if (token != null)
        {
            Claims claims = JwtUtils.parseToken(token);
            if (claims != null)
            {
                String tokenKey = (String) claims.get(Constants.LOGIN_USER_KEY);
                if (tokenKey != null)
                {
                    // 从Redis获取
                    return (LoginUser) redisTemplate.opsForValue().get(getRedisKey(tokenKey));
                }
            }
        }
        return null;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate)
    {
        TokenService.redisTemplate = redisTemplate;
    }

    /**
     * 获取token
     */
    public static String getToken(HttpServletRequest request)
    {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }
        return token;
    }

    /**
     * 刷新token
     */
    public void refreshToken(LoginUser loginUser)
    {
        if (loginUser != null)
        {
            loginUser.setLoginTime(System.currentTimeMillis());
            loginUser.setExpireTime(System.currentTimeMillis() + JwtUtils.EXPIRE);
            String redisKey = getRedisKey(loginUser.getToken());
            redisTemplate.opsForValue().set(redisKey, loginUser, JwtUtils.EXPIRE, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 删除用户token
     */
    public void removeToken(String token)
    {
        if (token != null && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.substring(Constants.TOKEN_PREFIX.length());
        }
        Claims claims = JwtUtils.parseToken(token);
        if (claims != null)
        {
            String tokenKey = (String) claims.get(Constants.LOGIN_USER_KEY);
            if (tokenKey != null)
            {
                redisTemplate.delete(getRedisKey(tokenKey));
            }
        }
    }

    private static String getRedisKey(String tokenKey)
    {
        return Constants.LOGIN_TOKEN_KEY + tokenKey;
    }
}
