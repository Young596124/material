package com.material.common.core.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.core.page.TableDataInfo;
import com.material.common.utils.ServletUtils;
import com.material.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * web层通用数据处理
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 构建分页对象
     */
    protected <T> Page<T> buildPage()
    {
        Integer pageNum = ServletUtils.getParameterToInt("pageNum", 1);
        Integer pageSize = ServletUtils.getParameterToInt("pageSize", 10);
        return new Page<>(pageNum, pageSize);
    }

    /**
     * 获取MyBatis Plus分页结果并封装为TableDataInfo
     */
    protected TableDataInfo getDataTable(IPage<?> page)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /**
     * 列表数据封装
     */
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }
}
