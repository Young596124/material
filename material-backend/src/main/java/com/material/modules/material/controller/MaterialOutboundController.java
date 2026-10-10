package com.material.modules.material.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.modules.material.domain.OutboundDTO;
import com.material.modules.material.service.IMaterialOutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物料出库 控制器（表单提交）
 */
@RestController
@RequestMapping("/material/outbound")
public class MaterialOutboundController extends BaseController
{
    @Autowired
    private IMaterialOutboundService outboundService;

    /**
     * 提交出库表单
     */
    @PostMapping
    public AjaxResult add(@RequestBody OutboundDTO dto)
    {
        return toAjax(outboundService.insertOutbound(dto));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
