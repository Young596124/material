package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.MaterialRecordExcelUtil;
import com.material.modules.material.domain.MaterialOutbound;
import com.material.modules.material.domain.MaterialOutboundDetail;
import com.material.modules.material.service.IMaterialOutboundService;
import com.material.modules.system.domain.SysDictData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 物料出库-记录详情 控制器
 */
@RestController
@RequestMapping("/material/outboundRecord")
public class MaterialOutboundRecordController extends BaseController
{
    @Autowired
    private IMaterialOutboundService outboundService;

    /**
     * 分页查询出库记录
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialOutbound outbound)
    {
        Page<MaterialOutbound> page = buildPage();
        Page<MaterialOutbound> result = outboundService.selectRecordList(page, outbound);
        return getDataTable(result);
    }

    /**
     * 根据出库ID查询记录（含明细，用于展开行）
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(outboundService.selectRecordById(id));
    }

    /**
     * 根据出库ID查询明细列表
     */
    @GetMapping("/detail/{outboundId}")
    public AjaxResult detail(@PathVariable Long outboundId)
    {
        return AjaxResult.success(outboundService.selectDetailListByOutboundId(outboundId));
    }

    /**
     * 导出出库记录（主表一个工作表、子表一个工作表，子表含出库编号列）
     */
    @PostMapping("/export")
    public void export(MaterialOutbound outbound, HttpServletResponse response) throws Exception
    {
        List<MaterialOutbound> mainList = outboundService.selectExportList(outbound);

        String[] mainHeaders = {"出库编号", "用户", "描述", "创建人", "创建时间"};
        List<Object[]> mainRows = new ArrayList<>();
        String[] detailHeaders = {"出库编号", "物料名称", "来源类型", "货位", "状态", "剩余数量", "出库数量"};
        List<Object[]> detailRows = new ArrayList<>();

        for (MaterialOutbound m : mainList)
        {
            mainRows.add(new Object[]{
                    m.getOutboundCode(), m.getUserName(), m.getDescription(),
                    m.getCreateBy(),
                    m.getCreateTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, m.getCreateTime())
            });
            if (m.getDetails() != null)
            {
                for (MaterialOutboundDetail d : m.getDetails())
                {
                    detailRows.add(new Object[]{
                            m.getOutboundCode(), d.getMaterialName(), d.getSourceType(), d.getLocation(),
                            d.getStatus(), d.getRemainingQuantity(), d.getOutboundQuantity()
                    });
                }
            }
        }

        MaterialRecordExcelUtil.export("出库记录", mainHeaders, mainRows,
                "出库明细", detailHeaders, detailRows, "物料出库记录详情.xlsx", response);
    }
}
