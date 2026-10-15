package com.material.modules.material.controller;

import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.modules.material.domain.MaterialCodeApplicationDTO;
import com.material.modules.material.service.IMaterialCodeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 物料编码申请 控制器
 */
@RestController
@RequestMapping("/material/codeApplication")
public class MaterialCodeApplicationController extends BaseController
{
    @Autowired
    private IMaterialCodeApplicationService applicationService;

    /**
     * 提交物料申请
     */
    @PostMapping
    public AjaxResult add(@RequestBody MaterialCodeApplicationDTO dto)
    {
        return toAjax(applicationService.insertApplication(dto));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
