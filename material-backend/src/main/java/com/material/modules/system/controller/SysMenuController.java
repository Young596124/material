package com.material.modules.system.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysMenu;
import com.material.modules.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单控制器
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    /**
     * 菜单列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return getDataTable(menus);
    }

    /**
     * 根据菜单ID查询详情
     */
    @GetMapping("/{menuId}")
    public AjaxResult getInfo(@PathVariable Long menuId)
    {
        return AjaxResult.success(menuService.selectMenuById(menuId));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public AjaxResult add(@RequestBody SysMenu menu)
    {
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysMenu menu)
    {
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable Long menuId)
    {
        return toAjax(menuService.deleteMenu(menuId));
    }

    /**
     * 响应返回结果
     */
    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
