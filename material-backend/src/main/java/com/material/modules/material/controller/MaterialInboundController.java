package com.material.modules.material.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.modules.material.domain.InboundDTO;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物料入库 控制器（表单提交）
 */
@RestController
@RequestMapping("/material/inbound")
public class MaterialInboundController extends BaseController
{
    @Autowired
    private IMaterialInboundService inboundService;

    /**
     * 提交入库表单
     */
    @PostMapping
    public AjaxResult add(@RequestBody InboundDTO dto)
    {
        return toAjax(inboundService.insertInbound(dto));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
