package com.material.common.utils;

/**
 * 字符串工具类
 */
public class StringUtils
{
    /** 空字符串 */
    private static final String NULLSTR = "";

    /** 下划线 */
    private static final char SEPARATOR = '_';

    public static boolean isEmpty(Object str)
    {
        return str == null || "".equals(str.toString().trim());
    }

    public static boolean isNotEmpty(Object str)
    {
        return !isEmpty(str);
    }

    public static boolean isNull(Object object)
    {
        return object == null;
    }

    public static boolean isNotNull(Object object)
    {
        return !isNull(object);
    }

    public static String trim(String str)
    {
        return str == null ? "" : str.trim();
    }

    public static String format(String template, Object... params)
    {
        if (isEmpty(params) || isEmpty(template))
        {
            return template;
        }
        return new StringBuilder().append(template).toString();
    }

    /**
     * 驼峰转下划线
     */
    public static String camelToUnderline(String str)
    {
        if (isEmpty(str))
        {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            char c = str.charAt(i);
            if (Character.isUpperCase(c))
            {
                sb.append(SEPARATOR);
                sb.append(Character.toLowerCase(c));
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     */
    public static String underlineToCamel(String str)
    {
        if (isEmpty(str))
        {
            return "";
        }
        str = str.toLowerCase();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (c == SEPARATOR && ++i < str.length())
            {
                sb.append(Character.toUpperCase(str.charAt(i)));
            }
            else
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
