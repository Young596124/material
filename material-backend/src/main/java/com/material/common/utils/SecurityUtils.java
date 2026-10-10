package com.material.common.utils;

import com.material.framework.manager.TokenService;
import com.material.modules.system.domain.LoginUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 权限获取工具类
 */
public class SecurityUtils
{
    /**
     * 获取用户ID
     */
    public static Long getUserId()
    {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null && loginUser.getUser() != null)
        {
            return loginUser.getUser().getUserId();
        }
        return null;
    }

    /**
     * 获取用户名称（账号）
     */
    public static String getUsername()
    {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null && loginUser.getUser() != null)
        {
            return loginUser.getUser().getUserName();
        }
        return null;
    }

    /**
     * 获取用户昵称
     */
    public static String getNickName()
    {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null && loginUser.getUser() != null)
        {
            return loginUser.getUser().getNickName();
        }
        return null;
    }

    /**
     * 获取登录用户
     */
    public static LoginUser getLoginUser()
    {
        try
        {
            return TokenService.getLoginUser(ServletUtils.getRequest());
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 生成BCrypt密码
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    /**
     * 判断密码是否相同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, encodedPassword);
    }
}
