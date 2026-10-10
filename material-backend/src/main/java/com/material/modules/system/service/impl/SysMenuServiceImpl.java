package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.material.common.utils.SecurityUtils;
import com.material.common.utils.StringUtils;
import com.material.modules.system.domain.SysMenu;
import com.material.modules.system.mapper.SysMenuMapper;
import com.material.modules.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 菜单业务实现
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 查询所有菜单（用于构建路由树）
     */
    @Override
    public List<SysMenu> selectMenuTreeAll()
    {
        return sysMenuMapper.selectMenuTreeAll();
    }

    /**
     * 查询菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu)
    {
        if (menu != null
                && (StringUtils.isNotEmpty(menu.getMenuName())
                || StringUtils.isNotEmpty(menu.getMenuType())
                || StringUtils.isNotEmpty(menu.getStatus())))
        {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>()
                    .like(StringUtils.isNotEmpty(menu.getMenuName()), SysMenu::getMenuName, menu.getMenuName())
                    .eq(StringUtils.isNotEmpty(menu.getMenuType()), SysMenu::getMenuType, menu.getMenuType())
                    .eq(StringUtils.isNotEmpty(menu.getStatus()), SysMenu::getStatus, menu.getStatus())
                    .orderByAsc(SysMenu::getParentId)
                    .orderByAsc(SysMenu::getOrderNum);
            return sysMenuMapper.selectList(wrapper);
        }
        return sysMenuMapper.selectMenuList();
    }

    /**
     * 构建菜单树
     */
    @Override
    public List<SysMenu> buildMenus(List<SysMenu> menus)
    {
        return getChildPerms(menus, 0L);
    }

    /**
     * 递归获取子菜单
     */
    @Override
    public List<SysMenu> getChildPerms(List<SysMenu> list, Long parentId)
    {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu menu : list)
        {
            if (parentId.equals(menu.getParentId()))
            {
                recursionFn(list, menu);
                returnList.add(menu);
            }
        }
        return returnList;
    }

    /**
     * 递归处理子菜单
     */
    private void recursionFn(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu menu : list)
        {
            if (t.getMenuId().equals(menu.getParentId()))
            {
                tlist.add(menu);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t)
    {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 新增菜单
     */
    @Override
    public int insertMenu(SysMenu menu)
    {
        menu.setCreateBy(SecurityUtils.getUsername());
        menu.setCreateTime(new Date());
        return sysMenuMapper.insert(menu);
    }

    /**
     * 修改菜单
     */
    @Override
    public int updateMenu(SysMenu menu)
    {
        menu.setUpdateBy(SecurityUtils.getUsername());
        menu.setUpdateTime(new Date());
        return sysMenuMapper.updateById(menu);
    }

    /**
     * 删除菜单
     */
    @Override
    public int deleteMenu(Long menuId)
    {
        return sysMenuMapper.deleteById(menuId);
    }

    /**
     * 根据菜单ID查询菜单
     */
    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        return sysMenuMapper.selectById(menuId);
    }
}
