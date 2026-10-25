package com.material.modules.consumable.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.ExcelExportUtil;
import com.material.modules.consumable.domain.ConsumablePick;
import com.material.modules.consumable.service.IConsumablePickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 低值易耗品-物品领用 控制器
 */
@RestController
@RequestMapping("/consumable/pick")
public class ConsumablePickController extends BaseController
{
    @Autowired
    private IConsumablePickService pickService;

    /**
     * 分页查询领用列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ConsumablePick pick)
    {
        Page<ConsumablePick> page = buildPage();
        Page<ConsumablePick> result = pickService.selectList(page, pick);
        return getDataTable(result);
    }

    /**
     * 根据ID查询领用详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(pickService.selectById(id));
    }

    /**
     * 新增领用
     */
    @PostMapping
    public AjaxResult add(@RequestBody ConsumablePick pick)
    {
        return AjaxResult.success(pickService.insertPick(pick));
    }

    /**
     * 导出领用记录
     */
    @PostMapping("/export")
    public void export(ConsumablePick pick, HttpServletResponse response) throws Exception
    {
        List<ConsumablePick> list = pickService.selectExportList(pick);
        String[] headers = {"领用编号", "货物类型", "货物名称", "货物编号", "领用总数", "单位",
                "库房名称", "库房编号", "库房地址", "责任部门", "备注", "创建人", "领用时间"};
        List<Object[]> rows = new ArrayList<>();
        for (ConsumablePick m : list)
        {
            rows.add(new Object[]{
                    m.getPickCode(), m.getCategoryName(), m.getGoodsName(), m.getGoodsCode(),
                    m.getQuantity(), m.getUnit(),
                    m.getStorehouseName(), m.getStorehouseCode(), m.getStorehouseAddress(), m.getDeptName(),
                    m.getRemark(), m.getCreateBy(),
                    m.getPickTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, m.getPickTime())
            });
        }
        ExcelExportUtil.export("物品领用", headers, rows, "低值易耗品领用记录.xlsx", response);
    }
}
