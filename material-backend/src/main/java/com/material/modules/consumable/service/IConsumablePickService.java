package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumablePick;

import java.util.List;

/**
 * 低值易耗品-物品领用 业务层
 */
public interface IConsumablePickService
{
    /** 分页查询领用列表 */
    Page<ConsumablePick> selectList(Page<ConsumablePick> page, ConsumablePick pick);

    /** 根据ID查询 */
    ConsumablePick selectById(Long id);

    /** 新增领用（自动生成领用编号并扣减库存） */
    int insertPick(ConsumablePick pick);

    /** 查询导出列表 */
    List<ConsumablePick> selectExportList(ConsumablePick pick);
}
