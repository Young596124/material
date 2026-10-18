package com.material.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.system.domain.Warehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库 数据层
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse>
{
}
