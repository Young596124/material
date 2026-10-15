package com.material.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysRole;
import com.material.modules.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysRole role)
    {
        Page<SysRole> page = buildPage();
        Page<SysRole> result = roleService.selectRoleList(page, role);
        return getDataTable(result);
    }

    /**
     * 查询全部角色（供用户管理下拉选择）
     */
    @GetMapping("/all")
    public AjaxResult all()
    {
        return AjaxResult.success(roleService.selectRoleAll());
    }

    /**
     * 根据角色ID查询详情(含已分配菜单ID)
     */
    @GetMapping("/{roleId}")
    public AjaxResult getInfo(@PathVariable Long roleId)
    {
        AjaxResult ajax = AjaxResult.success(roleService.selectRoleById(roleId));
        ajax.put("menuIds", roleService.selectMenuIdsByRoleId(roleId));
        return ajax;
    }

    /**
     * 新增角色
     */
    @PostMapping
    public AjaxResult add(@RequestBody SysRole role)
    {
        return toAjax(roleService.insertRole(role));
    }

    /**
     * 修改角色
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysRole role)
    {
        return toAjax(roleService.updateRole(role));
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds)
    {
        return toAjax(roleService.deleteRoleByIds(Arrays.asList(roleIds)));
    }

    /**
     * 分配菜单（保存角色-菜单关联）
     */
    @PutMapping("/dataScope")
    public AjaxResult assignMenu(@RequestBody SysRole roleData)
    {
        return toAjax(roleService.assignMenu(roleData.getRoleId(), roleData.getMenuIds()));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
