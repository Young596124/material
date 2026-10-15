package com.material.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialOutboundDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料出库子表 数据层
 */
@Mapper
public interface MaterialOutboundDetailMapper extends BaseMapper<MaterialOutboundDetail>
{
    /**
     * 根据出库主表ID查询明细列表
     */
    List<MaterialOutboundDetail> selectDetailListByOutboundId(@Param("outboundId") Long outboundId);

    /**
     * 分页查询出库数据管理列表
     */
    Page<MaterialOutboundDetail> selectOutboundDataList(Page<MaterialOutboundDetail> page, @Param("detail") MaterialOutboundDetail detail);

    /**
     * 根据出库主表ID批量查询明细列表（用于导出）
     */
    List<MaterialOutboundDetail> selectDetailListByOutboundIds(@Param("outboundIds") List<Long> outboundIds);
}
