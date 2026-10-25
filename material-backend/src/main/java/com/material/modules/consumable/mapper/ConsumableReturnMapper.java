package com.material.modules.consumable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.consumable.domain.ConsumableReturn;
import org.apache.ibatis.annotations.Mapper;

/**
 * 低值易耗品-物品退库 Mapper
 */
@Mapper
public interface ConsumableReturnMapper extends BaseMapper<ConsumableReturn>
{
}
