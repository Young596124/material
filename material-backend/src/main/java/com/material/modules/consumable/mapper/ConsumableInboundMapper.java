package com.material.modules.consumable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.consumable.domain.ConsumableInbound;
import org.apache.ibatis.annotations.Mapper;

/**
 * 低值易耗品-物品入库 Mapper
 */
@Mapper
public interface ConsumableInboundMapper extends BaseMapper<ConsumableInbound>
{
}
