package com.material.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.system.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 数据层
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole>
{
}
