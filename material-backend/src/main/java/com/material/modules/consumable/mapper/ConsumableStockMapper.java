package com.material.modules.consumable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.consumable.domain.ConsumableStock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 低值易耗品-库存信息 Mapper
 */
@Mapper
public interface ConsumableStockMapper extends BaseMapper<ConsumableStock>
{
}
