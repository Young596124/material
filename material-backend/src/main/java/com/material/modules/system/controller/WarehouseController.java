package com.material.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.system.domain.Warehouse;
import com.material.modules.system.service.IWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 仓库管理 控制器（系统管理）
 */
@RestController
@RequestMapping("/system/warehouse")
public class WarehouseController extends BaseController
{
    @Autowired
    private IWarehouseService warehouseService;

    /**
     * 分页查询仓库列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Warehouse warehouse)
    {
        Page<Warehouse> page = buildPage();
        Page<Warehouse> result = warehouseService.selectList(page, warehouse);
        return getDataTable(result);
    }

    /**
     * 根据ID查询仓库详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(warehouseService.selectById(id));
    }

    /**
     * 查询全部启用的仓库（供下拉选择）
     */
    @GetMapping("/selectEnabled")
    public AjaxResult selectEnabled()
    {
        return AjaxResult.success(warehouseService.selectEnabled());
    }

    /**
     * 新增仓库
     */
    @PostMapping
    public AjaxResult add(@RequestBody Warehouse warehouse)
    {
        return toAjax(warehouseService.insertWarehouse(warehouse));
    }

    /**
     * 修改仓库
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Warehouse warehouse)
    {
        return toAjax(warehouseService.updateWarehouse(warehouse));
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(warehouseService.deleteByIds(Arrays.asList(ids)));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
