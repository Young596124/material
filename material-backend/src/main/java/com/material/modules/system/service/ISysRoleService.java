package com.material.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.SysRole;

import java.util.List;

/**
 * 角色业务层
 */
public interface ISysRoleService
{
    /** 分页查询 */
    Page<SysRole> selectRoleList(Page<SysRole> page, SysRole role);

    /** 查询全部角色 */
    List<SysRole> selectRoleAll();

    /** 根据ID查询 */
    SysRole selectRoleById(Long roleId);

    /** 新增 */
    int insertRole(SysRole role);

    /** 修改 */
    int updateRole(SysRole role);

    /** 删除 */
    int deleteRoleByIds(List<Long> roleIds);

    /** 根据角色ID查询已分配的菜单ID */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /** 分配菜单(保存角色-菜单关联) */
    int assignMenu(Long roleId, List<Long> menuIds);
}
