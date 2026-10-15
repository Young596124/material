package com.material.modules.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色-菜单关联 数据层
 */
@Mapper
public interface SysRoleMenuMapper
{
    /**
     * 查询角色已分配的菜单ID集合
     */
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除角色菜单关联
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增角色菜单关联
     */
    int batchInsert(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
}
