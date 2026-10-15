package com.material.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialInbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物料入库主表 数据层
 */
@Mapper
public interface MaterialInboundMapper extends BaseMapper<MaterialInbound>
{
    /**
     * 分页查询入库记录
     */
    Page<MaterialInbound> selectInboundList(Page<MaterialInbound> page, @Param("inbound") MaterialInbound inbound);

    /**
     * 查询入库记录（不分页，用于导出）
     */
    List<MaterialInbound> selectInboundExportList(@Param("inbound") MaterialInbound inbound);
}
