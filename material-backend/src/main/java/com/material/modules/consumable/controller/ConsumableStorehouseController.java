package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.consumable.domain.ConsumableStorehouse;
import com.material.modules.consumable.service.IConsumableStorehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 低值易耗品-库房 控制器
 */
@RestController
@RequestMapping("/consumable/storehouse")
public class ConsumableStorehouseController extends BaseController
{
    @Autowired
    private IConsumableStorehouseService storehouseService;

    /**
     * 分页查询库房列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableStorehouse storehouse)
    {
        Page<ConsumableStorehouse> page = buildPage();
        Page<ConsumableStorehouse> result = storehouseService.selectList(page, storehouse);
        return getDataTable(result);
    }

    /**
     * 查询全部启用的库房（供弹框选择）
     */
    @GetMapping("/selectEnabled")
    public AjaxResult selectEnabled()
    {
        return AjaxResult.success(storehouseService.selectEnabled());
    }

    /**
     * 根据ID查询库房详情
     */
    @GetMapping("/{storehouseId}")
    public AjaxResult getInfo(@PathVariable Long storehouseId)
    {
        return AjaxResult.success(storehouseService.selectById(storehouseId));
    }

    /**
     * 新增库房
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumableStorehouse storehouse)
    {
        return toAjax(storehouseService.insertStorehouse(storehouse));
    }

    /**
     * 修改库房
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ConsumableStorehouse storehouse)
    {
        return toAjax(storehouseService.updateStorehouse(storehouse));
    }

    /**
     * 修改状态
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConsumableStorehouse storehouse)
    {
        return toAjax(storehouseService.changeStatus(storehouse));
    }

    /**
     * 删除库房
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(storehouseService.deleteByIds(Arrays.asList(ids)));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
