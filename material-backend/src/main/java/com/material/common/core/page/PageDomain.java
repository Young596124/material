package com.material.common.core.page;

import com.material.common.utils.ServletUtils;

/**
 * 分页数据
 */
public class PageDomain
{
    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页数量 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String isAsc;

    public static PageDomain build()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt("pageNum"));
        pageDomain.setPageSize(ServletUtils.getParameterToInt("pageSize"));
        pageDomain.setOrderByColumn(ServletUtils.getParameter("orderByColumn"));
        pageDomain.setIsAsc(ServletUtils.getParameter("isAsc"));
        return pageDomain;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        this.isAsc = isAsc;
    }
}
