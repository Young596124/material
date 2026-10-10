package com.material.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialInboundDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料入库子表 数据层
 */
@Mapper
public interface MaterialInboundDetailMapper extends BaseMapper<MaterialInboundDetail>
{
    /**
     * 根据入库主表ID查询明细列表
     */
    List<MaterialInboundDetail> selectDetailListByInboundId(@Param("inboundId") Long inboundId);

    /**
     * 分页查询入库数据管理列表
     */
    Page<MaterialInboundDetail> selectInboundDataList(Page<MaterialInboundDetail> page, @Param("detail") MaterialInboundDetail detail);
}
