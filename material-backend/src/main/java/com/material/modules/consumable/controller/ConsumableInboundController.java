
package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.ExcelExportUtil;
import com.material.modules.consumable.domain.ConsumableInbound;
import com.material.modules.consumable.service.IConsumableInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 低值易耗品-物品入库 控制器
 */
@RestController
@RequestMapping("/consumable/inbound")
public class ConsumableInboundController extends BaseController
{
    @Autowired
    private IConsumableInboundService inboundService;

    /**
     * 分页查询入库列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumableInbound inbound)
    {
        Page<ConsumableInbound> page = buildPage();
        Page<ConsumableInbound> result = inboundService.selectList(page, inbound);
        return getDataTable(result);
    }

    /**
     * 根据ID查询入库详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(inboundService.selectById(id));
    }

    /**
     * 新增入库
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumableInbound inbound)
    {
        return AjaxResult.success(inboundService.insertInbound(inbound));
    }

    /**
     * 导出入库记录
     */
    @PostMapping("/export")
    public void export(ConsumableInbound inbound, HttpServletResponse response) throws Exception
    {
        List<ConsumableInbound> list = inboundService.selectExportList(inbound);
        String[] headers = {"入库编号", "货物类型", "货物名称", "货物编号", "入库总数", "单位",
                "库房名称", "库房编号", "库房地址", "责任部门", "备注", "创建人", "入库时间"};
        List<Object[]> rows = new ArrayList<>();
        for (ConsumableInbound m : list)
        {
            rows.add(new Object[]{
                    m.getInboundCode(), m.getCategoryName(), m.getGoodsName(), m.getGoodsCode(),
                    m.getQuantity(), m.getUnit(),
                    m.getStorehouseName(), m.getStorehouseCode(), m.getStorehouseAddress(), m.getDeptName(),
                    m.getRemark(), m.getCreateBy(),
                    m.getInboundTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, m.getInboundTime())
            });
        }
        ExcelExportUtil.export("物品入库", headers, rows, "低值易耗品入库记录.xlsx", response);
    }
}
