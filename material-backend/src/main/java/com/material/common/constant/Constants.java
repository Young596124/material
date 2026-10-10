package com.material.common.constant;

/**
 * 通用常量
 */
public class Constants
{
    /** UTF-8 编码 */
    public static final String UTF8 = "UTF-8";

    /** 成功标记 */
    public static final Integer SUCCESS = 200;

    /** 失败标记 */
    public static final Integer FAIL = 500;

    /** 登录Token */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 令牌前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 令牌前缀 */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /** 资源映射路径前缀 */
    public static final String RESOURCE_PREFIX = "/profile";

    /** 自动识别的文件类型 */
    public static final String[] IMAGE_FILE_EXT = { "png", "jpg", "jpeg", "gif" };
}
