package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.consumable.domain.ConsumableGoods;
import com.material.modules.consumable.service.IConsumableGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 低值易耗品-物品信息 控制器
 */
@RestController
@RequestMapping("/consumable/goods")
public class ConsumableGoodsController extends BaseController
{
    @Autowired
    private IConsumableGoodsService goodsService;

    /**
     * 分页查询物品列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableGoods goods)
    {
        Page<ConsumableGoods> page = buildPage();
        Page<ConsumableGoods> result = goodsService.selectList(page, goods);
        return getDataTable(result);
    }

    /**
     * 根据类别ID查询启用且显示的物品（供联动下拉）
     */
    @GetMapping("/listByCategory/{categoryId}")
    public AjaxResult listByCategory(@PathVariable Long categoryId)
    {
        return AjaxResult.success(goodsService.selectByCategoryId(categoryId));
    }

    /**
     * 根据ID查询物品详情
     */
    @GetMapping("/{goodsId}")
    public AjaxResult getInfo(@PathVariable Long goodsId)
    {
        return AjaxResult.success(goodsService.selectById(goodsId));
    }

    /**
     * 新增物品
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumableGoods goods)
    {
        return toAjax(goodsService.insertGoods(goods));
    }

    /**
     * 修改物品
     */
    @PutMapping
    public AjaxResult edit(@RequestBody ConsumableGoods goods)
    {
        return toAjax(goodsService.updateGoods(goods));
    }

    /**
     * 修改状态
     */
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody ConsumableGoods goods)
    {
        return toAjax(goodsService.changeStatus(goods));
    }

    /**
     * 删除物品
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(goodsService.deleteByIds(Arrays.asList(ids)));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
