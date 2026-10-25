package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.consumable.domain.ConsumableCategory;
import com.material.modules.consumable.service.IConsumableCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 低值易耗品-物品类别 控制器
 */
@RestController
@RequestMapping("/consumable/category")
public class ConsumableCategoryController extends BaseController
{
    @Autowired
    private IConsumableCategoryService categoryService;

    /**
     * 分页查询类别列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableCategory category)
    {
        Page<ConsumableCategory> page = buildPage();
        Page<ConsumableCategory> result = categoryService.selectList(page, category);
        return getDataTable(result);
    }

    /**
     * 查询全部启用的类别（供下拉选择）
     */
    @GetMapping("/selectEnabled")
    public AjaxResult selectEnabled()
    {
        return AjaxResult.success(categoryService.selectEnabled());
    }

    /**
     * 根据ID查询类别详情
     */
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return AjaxResult.success(categoryService.selectById(categoryId));
    }

    /**
     * 新增类别
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumableCategory category)
    {
        return toAjax(categoryService.insertCategory(category));
    }

    /**
     * 修改类别
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ConsumableCategory category)
    {
        return toAjax(categoryService.updateCategory(category));
    }

    /**
     * 修改状态
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConsumableCategory category)
    {
        return toAjax(categoryService.changeStatus(category));
    }

    /**
     * 删除类别
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(categoryService.deleteByIds(Arrays.asList(ids)));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
