package com.material.modules.system.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户身份权限类
 */
@Data
public class LoginUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户信息 */
    private SysUser user;

    /** token */
    private String token;

    /** 登录IP */
    private String ipaddr;

    /** 登录时间 */
    private Long loginTime;

    /** 过期时间 */
    private Long expireTime;
}
