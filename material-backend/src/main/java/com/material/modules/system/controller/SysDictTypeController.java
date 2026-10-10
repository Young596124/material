package com.material.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysDictType;
import com.material.modules.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 字典类型控制器
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController
{
    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType)
    {
        Page<SysDictType> page = buildPage();
        Page<SysDictType> result = dictTypeService.selectDictTypeList(page, dictType);
        return getDataTable(result);
    }

    /**
     * 根据字典ID查询详情
     */
    @GetMapping("/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    public AjaxResult add(@RequestBody SysDictType dictType)
    {
        return toAjax(dictTypeService.insertDictType(dictType));
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    public AjaxResult edit(@RequestBody SysDictType dictType)
    {
        return toAjax(dictTypeService.updateDictType(dictType));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        int count = dictTypeService.deleteDictTypeByIds(Arrays.asList(dictIds));
        return toAjax(count);
    }

    /**
     * 响应返回结果
     */
    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
