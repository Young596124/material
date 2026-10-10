package com.material.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.SysDictData;
import com.material.modules.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private ISysDictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData)
    {
        Page<SysDictData> page = buildPage();
        Page<SysDictData> result = dictDataService.selectDictDataList(page, dictData);
        return getDataTable(result);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @GetMapping("/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        List<SysDictData> list = dictDataService.selectDictDataByType(dictType);
        return AjaxResult.success(list);
    }
}
