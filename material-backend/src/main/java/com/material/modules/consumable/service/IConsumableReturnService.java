package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableReturn;

import java.util.List;

/**
 * 低值易耗品-物品退库 业务层
 */
public interface IConsumableReturnService
{
    /** 分页查询退库列表 */
    Page<ConsumableReturn> selectList(Page<ConsumableReturn> page, ConsumableReturn returnRecord);

    /** 根据ID查询 */
    ConsumableReturn selectById(Long id);

    /** 新增退库（自动生成退库编号并回退库存） */
    int insertReturn(ConsumableReturn returnRecord);

    /** 查询导出列表 */
    List<ConsumableReturn> selectExportList(ConsumableReturn returnRecord);
}
