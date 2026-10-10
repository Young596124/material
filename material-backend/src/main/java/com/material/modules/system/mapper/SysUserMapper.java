package com.material.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 数据层
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser>
{
    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 查询用户列表
     */
    List<SysUser> selectUserList(@Param("user") SysUser user);
}
