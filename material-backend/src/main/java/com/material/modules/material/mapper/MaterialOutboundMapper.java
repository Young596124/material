package com.material.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialOutbound;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物料出库主表 数据层
 */
@Mapper
public interface MaterialOutboundMapper extends BaseMapper<MaterialOutbound>
{
    /**
     * 分页查询出库记录
     */
    Page<MaterialOutbound> selectOutboundList(Page<MaterialOutbound> page, @Param("outbound") MaterialOutbound outbound);
}
