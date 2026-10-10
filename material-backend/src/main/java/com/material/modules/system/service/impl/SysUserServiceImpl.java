package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.system.domain.SysUser;
import com.material.modules.system.mapper.SysUserMapper;
import com.material.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户业务实现
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 根据用户名查询用户
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return sysUserMapper.selectUserByUserName(userName);
    }

    /**
     * 根据用户ID查询用户
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return sysUserMapper.selectById(userId);
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public Page<SysUser> selectUserList(Page<SysUser> page, SysUser user)
    {
        return sysUserMapper.selectPage(page,
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDelFlag, "0")
                        .like(user.getUserName() != null && !"".equals(user.getUserName()),
                                SysUser::getUserName, user.getUserName())
                        .like(user.getNickName() != null && !"".equals(user.getNickName()),
                                SysUser::getNickName, user.getNickName())
                        .like(user.getPhonenumber() != null && !"".equals(user.getPhonenumber()),
                                SysUser::getPhonenumber, user.getPhonenumber())
                        .like(user.getEmployeeNo() != null && !"".equals(user.getEmployeeNo()),
                                SysUser::getEmployeeNo, user.getEmployeeNo())
                        .eq(user.getStatus() != null && !"".equals(user.getStatus()),
                                SysUser::getStatus, user.getStatus())
                        .orderByAsc(SysUser::getUserId));
    }

    /**
     * 新增用户
     */
    @Override
    public int insertUser(SysUser user)
    {
        // 密码加密
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setCreateBy(SecurityUtils.getUsername());
        user.setCreateTime(new Date());
        return sysUserMapper.insert(user);
    }

    /**
     * 修改用户
     */
    @Override
    public int updateUser(SysUser user)
    {
        user.setUpdateBy(SecurityUtils.getUsername());
        user.setUpdateTime(new Date());
        return sysUserMapper.updateById(user);
    }

    /**
     * 删除用户
     */
    @Override
    public int deleteUser(Long userId)
    {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setDelFlag("2");
        user.setUpdateBy(SecurityUtils.getUsername());
        user.setUpdateTime(new Date());
        return sysUserMapper.updateById(user);
    }

    /**
     * 校验用户名是否唯一
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, user.getUserName())
                .eq(SysUser::getDelFlag, "0"));
        if (info != null && !info.getUserId().equals(userId))
        {
            return false;
        }
        return true;
    }
}
