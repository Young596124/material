package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.ExcelExportUtil;
import com.material.modules.consumable.domain.ConsumableReturn;
import com.material.modules.consumable.service.IConsumableReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 低值易耗品-物品退库 控制器
 */
@RestController
@RequestMapping("/consumable/return")
public class ConsumableReturnController extends BaseController
{
    @Autowired
    private IConsumableReturnService returnService;

    /**
     * 分页查询退库列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableReturn returnRecord)
    {
        Page<ConsumableReturn> page = buildPage();
        Page<ConsumableReturn> result = returnService.selectList(page, returnRecord);
        return getDataTable(result);
    }

    /**
     * 根据ID查询退库详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(returnService.selectById(id));
    }

    /**
     * 新增退库
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumableReturn returnRecord)
    {
        return AjaxResult.success(returnService.insertReturn(returnRecord));
    }

    /**
     * 导出退库记录
     */
    @PostMapping("/export")
    public void export(ConsumableReturn returnRecord, HttpServletResponse response) throws Exception
    {
        List<ConsumableReturn> list = returnService.selectExportList(returnRecord);
        String[] headers = {"退库编号", "货物类型", "货物名称", "货物编号", "退库总数", "单位",
                "库房名称", "库房编号", "库房地址", "责任部门", "备注", "创建人", "退库时间"};
        List<Object[]> rows = new ArrayList<>();
        for (ConsumableReturn m : list)
        {
            rows.add(new Object[]{
                    m.getReturnCode(), m.getCategoryName(), m.getGoodsName(), m.getGoodsCode(),
                    m.getQuantity(), m.getUnit(),
                    m.getStorehouseName(), m.getStorehouseCode(), m.getStorehouseAddress(), m.getDeptName(),
                    m.getRemark(), m.getCreateBy(),
                    m.getReturnTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, m.getReturnTime())
            });
        }
        ExcelExportUtil.export("物品退库", headers, rows, "低值易耗品退库记录.xlsx", response);
    }
}
