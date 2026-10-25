package com.material.modules.consumable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.consumable.domain.ConsumableGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 低值易耗品-物品信息 Mapper
 */
@Mapper
public interface ConsumableGoodsMapper extends BaseMapper<ConsumableGoods>
{
}
