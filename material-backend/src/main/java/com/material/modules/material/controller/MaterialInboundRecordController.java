package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.material.domain.MaterialInbound;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
