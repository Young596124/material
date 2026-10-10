package com.material.common.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 客户端工具类
 */
public class ServletUtils
{
    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return getParameterToInt(name, null);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        String value = getParameter(name);
        if (StringUtils.isEmpty(value))
        {
            return defaultValue;
        }
        try
        {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }
}
