package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableStock;

import java.util.List;

/**
 * 低值易耗品-库存信息 业务层
 */
public interface IConsumableStockService
{
    /** 分页查询库存列表 */
    Page<ConsumableStock> selectList(Page<ConsumableStock> page, ConsumableStock stock);

    /** 查询导出列表 */
    List<ConsumableStock> selectExportList(ConsumableStock stock);
}
