package com.material.modules.system.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysDept;
import com.material.modules.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 部门列表（平铺，前端组装树）
     */
    @GetMapping("/list")
    public TableDataInfo list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return getDataTable(depts);
    }

    /**
     * 部门树结构
     */
    @GetMapping("/tree")
    public AjaxResult tree()
    {
        return AjaxResult.success(deptService.buildDeptTree(deptService.selectDeptList(null)));
    }

    /**
     * 根据部门ID查询详情
     */
    @GetMapping("/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @PostMapping
    public AjaxResult add(@RequestBody SysDept dept)
    {
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysDept dept)
    {
        if (dept.getDeptId() != null && dept.getDeptId().equals(dept.getParentId()))
        {
            return AjaxResult.error("上级部门不能是自己");
        }
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildDept(deptId))
        {
            return AjaxResult.error("存在下级部门，不允许删除");
        }
        if (deptService.hasUser(deptId))
        {
            return AjaxResult.error("部门存在用户，不允许删除");
        }
        return toAjax(deptService.deleteDept(deptId));
    }

    /**
     * 响应返回结果
     */
    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
