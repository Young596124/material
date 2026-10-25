package com.material.modules.consumable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.consumable.domain.ConsumablePick;
import org.apache.ibatis.annotations.Mapper;

/**
 * 低值易耗品-物品领用 Mapper
 */
@Mapper
public interface ConsumablePickMapper extends BaseMapper<ConsumablePick>
{
}
