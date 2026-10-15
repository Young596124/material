package com.material.modules.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.controller.BaseController;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.DateUtils;
import com.material.common.utils.MaterialRecordDataExcelUtil;
import com.material.modules.material.domain.MaterialInbound;
import com.material.modules.material.domain.MaterialInboundDetail;
import com.material.modules.material.mapper.MaterialInboundDetailMapper;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

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


    /**
     * 导出物料入库数据管理 Excel
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response) throws Exception
    {

        List<MaterialInboundDetail> list = inboundService.selectInboundDataExportList();
        String[] mainHeaders = {"物料名称", "来源类型", "货位", "状态", "剩余数量", "入库数量","创建人","创建时间"};
        List<Object[]> mainRows = new ArrayList<>();
        for (MaterialInboundDetail detail : list) {
            mainRows.add(new Object[]{
                    detail.getMaterialName(),detail.getSourceType(),detail.getLocation(),detail.getStatus(),
                    detail.getRemainingQuantity(),detail.getInboundQuantity(),detail.getCreateBy(),
                    detail.getCreateTime() == null ? "" : DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, detail.getCreateTime())

            });
        }

        MaterialRecordDataExcelUtil.export("入库数据管理", mainHeaders, mainRows, "物料入库数据管理.xlsx", response);
    }
}
