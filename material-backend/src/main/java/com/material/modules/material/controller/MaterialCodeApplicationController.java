package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.modules.material.domain.MaterialCodeApplication;
import com.material.modules.material.service.IMaterialCodeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

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
     * 分页查询列表
     */
    @GetMapping("/list")
    public TableDataInfo list(MaterialCodeApplication application)
    {
        Page<MaterialCodeApplication> page = buildPage();
        Page<MaterialCodeApplication> result = applicationService.selectList(page, application);
        return getDataTable(result);
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(applicationService.selectById(id));
    }

    /**
     * 新增（物料编码自动生成）
     */
    @PostMapping
    public AjaxResult add(@RequestBody MaterialCodeApplication application)
    {
        return toAjax(applicationService.insertApplication(application));
    }

    /**
     * 修改
     */
    @PutMapping
    public AjaxResult edit(@RequestBody MaterialCodeApplication application)
    {
        return toAjax(applicationService.updateApplication(application));
    }

    /**
     * 删除
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(applicationService.deleteByIds(Arrays.asList(ids)));
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
