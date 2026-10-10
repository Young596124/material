package com.material.modules.system.service;

import com.material.modules.system.domain.SysMenu;

import java.util.List;

/**
 * 菜单业务层
 */
public interface ISysMenuService
{
    /**
     * 查询所有菜单（用于构建路由树）
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 查询菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 构建菜单树
     */
    List<SysMenu> buildMenus(List<SysMenu> menus);

    /**
     * 递归获取子菜单
     */
    List<SysMenu> getChildPerms(List<SysMenu> list, Long parentId);

    /**
     * 新增菜单
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    int deleteMenu(Long menuId);

    /**
     * 根据菜单ID查询菜单
     */
    SysMenu selectMenuById(Long menuId);
}
