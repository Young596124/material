package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.material.domain.MaterialInboundDetail;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物料入库-数据管理 控制器
 */
@RestController
@RequestMapping("/material/inboundData")
public class MaterialInboundDataController extends BaseController
{
    @Autowired
    private IMaterialInboundService inboundService;

    /**
     * 分页查询入库数据（来源：入库子表）
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialInboundDetail detail)
    {
        Page<MaterialInboundDetail> page = buildPage();
        Page<MaterialInboundDetail> result = inboundService.selectDataList(page, detail);
        return getDataTable(result);
    }
}
