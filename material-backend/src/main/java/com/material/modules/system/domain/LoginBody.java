package com.material.modules.system.domain;

import lombok.Data;

/**
 * 用户登录对象
 */
@Data
public class LoginBody
{
    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
