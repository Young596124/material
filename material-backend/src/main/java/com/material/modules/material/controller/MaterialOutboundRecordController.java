package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.material.domain.MaterialOutbound;
import com.material.modules.material.service.IMaterialOutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
