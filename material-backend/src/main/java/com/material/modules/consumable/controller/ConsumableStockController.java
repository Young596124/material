package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.ExcelExportUtil;
import com.material.modules.consumable.domain.ConsumableStock;
import com.material.modules.consumable.service.IConsumableStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 低值易耗品-库存信息 控制器
 */
@RestController
@RequestMapping("/consumable/stock")
public class ConsumableStockController extends BaseController
{
    @Autowired
    private IConsumableStockService stockService;

    /**
     * 分页查询库存列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableStock stock)
    {
        Page<ConsumableStock> page = buildPage();
        Page<ConsumableStock> result = stockService.selectList(page, stock);
        return getDataTable(result);
    }

    /**
     * 导出库存信息
     */
    @PostMapping("/export")
    public void export(ConsumableStock stock, HttpServletResponse response) throws Exception
    {
        List<ConsumableStock> list = stockService.selectExportList(stock);
        String[] headers = {"货物类型", "货物等级", "货物名称", "货物编号", "库存数量", "入库数量",
                "领用数量", "退库数量", "单位", "货位号", "库房名称", "库房编号", "库房地址", "管理员"};
        List<Object[]> rows = new ArrayList<>();
        for (ConsumableStock m : list)
        {
            rows.add(new Object[]{
                    m.getCategoryName(), m.getGoodsLevel(), m.getGoodsName(), m.getGoodsCode(),
                    m.getStockQuantity(), m.getInboundQuantity(), m.getUsedQuantity(), m.getReturnQuantity(),
                    m.getUnit(), m.getLocation(),
                    m.getStorehouseName(), m.getStorehouseCode(), m.getStorehouseAddress(), m.getManager()
            });
        }
        ExcelExportUtil.export("库存信息", headers, rows, "低值易耗品库存信息.xlsx", response);
    }
}
