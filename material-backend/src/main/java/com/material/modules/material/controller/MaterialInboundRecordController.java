package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.MaterialRecordExcelUtil;
import com.material.modules.material.domain.MaterialInbound;
import com.material.modules.material.domain.MaterialInboundDetail;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 物料入库-记录详情 控制器
 */
@RestController
@RequestMapping("/material/inboundRecord")
public class MaterialInboundRecordController extends BaseController
{
    @Autowired
    private IMaterialInboundService inboundService;

    /**
     * 分页查询入库记录
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialInbound inbound)
    {
        Page<MaterialInbound> page = buildPage();
        Page<MaterialInbound> result = inboundService.selectRecordList(page, inbound);
        return getDataTable(result);
    }

    /**
     * 根据入库ID查询记录（含明细，用于展开行）
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(inboundService.selectRecordById(id));
    }

    /**
     * 根据入库ID查询明细列表
     */
    @GetMapping("/detail/{inboundId}")
    public AjaxResult detail(@PathVariable Long inboundId)
    {
        return AjaxResult.success(inboundService.selectDetailListByInboundId(inboundId));
    }

    /**
     * 导出入库记录（主表一个工作表、子表一个工作表，子表含入库编号列）
     */
    @PostMapping("/export")
    public void export(MaterialInbound inbound, HttpServletResponse response) throws Exception
    {
        List<MaterialInbound> mainList = inboundService.selectExportList(inbound);

        String[] mainHeaders = {"入库编号", "用户", "入库日期", "描述", "创建人", "创建时间"};
        List<Object[]> mainRows = new ArrayList<>();
        String[] detailHeaders = {"入库编号", "物料名称", "来源类型", "货位", "状态", "剩余数量", "入库数量"};
        List<Object[]> detailRows = new ArrayList<>();

        for (MaterialInbound m : mainList)
        {
            mainRows.add(new Object[]{
                    m.getInboundCode(), m.getUserName(),
                    m.getInboundDate() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, m.getInboundDate()),
                    m.getDescription(), m.getCreateBy(),
                    m.getCreateTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, m.getCreateTime())
            });
            if (m.getDetails() != null)
            {
                for (MaterialInboundDetail d : m.getDetails())
                {
                    detailRows.add(new Object[]{
                            m.getInboundCode(), d.getMaterialName(), d.getSourceType(), d.getLocation(),
                            d.getStatus(), d.getRemainingQuantity(), d.getInboundQuantity()
                    });
                }
            }
        }

        MaterialRecordExcelUtil.export("入库记录", mainHeaders, mainRows,
                "入库明细", detailHeaders, detailRows, "物料入库记录详情.xlsx", response);
    }
}
