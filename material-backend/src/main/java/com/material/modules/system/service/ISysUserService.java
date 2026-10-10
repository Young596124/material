package com.material.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.SysUser;

/**
 * 用户业务层
 */
public interface ISysUserService
{
    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(Long userId);

    /**
     * 分页查询用户列表
     */
    Page<SysUser> selectUserList(Page<SysUser> page, SysUser user);

    /**
     * 新增用户
     */
    int insertUser(SysUser user);

    /**
     * 修改用户
     */
    int updateUser(SysUser user);

    /**
     * 删除用户
     */
    int deleteUser(Long userId);

    /**
     * 校验用户名是否唯一
     */
    boolean checkUserNameUnique(SysUser user);
}
