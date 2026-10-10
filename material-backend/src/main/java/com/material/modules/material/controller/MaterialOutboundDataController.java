package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.material.domain.MaterialOutboundDetail;
import com.material.modules.material.service.IMaterialOutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 物料出库-数据管理 控制器
 */
@RestController
@RequestMapping("/material/outboundData")
public class MaterialOutboundDataController extends BaseController
{
    @Autowired
    private IMaterialOutboundService outboundService;

    /**
     * 分页查询出库数据（来源：出库子表）
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialOutboundDetail detail)
    {
        Page<MaterialOutboundDetail> page = buildPage();
        Page<MaterialOutboundDetail> result = outboundService.selectDataList(page, detail);
        return getDataTable(result);
    }
}
