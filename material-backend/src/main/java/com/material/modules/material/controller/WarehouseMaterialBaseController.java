package com.material.modules.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.domain.AjaxResult;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.WarehouseBaseExcelUtil;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.service.IWarehouseMaterialBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 1F线边仓物料基表 控制器
 */
@RestController
@RequestMapping("/material/warehouseBase")
public class WarehouseMaterialBaseController extends BaseController
{
    @Autowired
    private IWarehouseMaterialBaseService baseService;

    /**
     * 分页查询列表
     */
    @GetMapping("/list")
    public TableDataInfo list(WarehouseMaterialBase base)
    {
        Page<WarehouseMaterialBase> page = buildPage();
        Page<WarehouseMaterialBase> result = baseService.selectList(page, base);
        return getDataTable(result);
    }

    /**
     * 根据ID查询详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return AjaxResult.success(baseService.selectById(id));
    }

    /**
     * 修改物料基表
     */
    @PutMapping
    public AjaxResult edit(@RequestBody WarehouseMaterialBase base)
    {
        return toAjax(baseService.updateBase(base));
    }

    /**
     * 查询全部物料（供入库/出库下拉选择）
     */
    @GetMapping("/selectAll")
    public AjaxResult selectAll()
    {
        return AjaxResult.success(baseService.selectAll());
    }

    /**
     * 根据物料名称模糊查询（供入库/出库选择物料）
     */
    @GetMapping("/selectByName")
    public AjaxResult selectByName(@RequestParam String materialName)
    {
        return AjaxResult.success(baseService.selectByMaterialName(materialName));
    }

    /**
     * 导出物料基表 Excel
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws Exception
    {
        List<WarehouseMaterialBase> list = baseService.selectAll();
        // 导出时将来源类型字典值转换为标签，便于阅读
        baseService.convertSourceTypeValueToLabel(list);
        WarehouseBaseExcelUtil.export(list, response);
    }

    /**
     * 导入物料基表 Excel
     */
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file) throws Exception
    {
        List<WarehouseMaterialBase> list = WarehouseBaseExcelUtil.importExcel(file);
        int count = baseService.importBatch(list);
        return AjaxResult.success("导入成功，共处理 " + count + " 条数据");
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        WarehouseBaseExcelUtil.downloadTemplate(response);
    }

    private AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
