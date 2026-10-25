package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableInbound;

import java.util.List;

/**
 * 低值易耗品-物品入库 业务层
 */
public interface IConsumableInboundService
{
    /** 分页查询入库列表 */
    Page<ConsumableInbound> selectList(Page<ConsumableInbound> page, ConsumableInbound inbound);

    /** 根据ID查询 */
    ConsumableInbound selectById(Long id);

    /** 新增入库（自动生成入库编号并累加库存） */
    int insertInbound(ConsumableInbound inbound);

    /** 查询导出列表 */
    List<ConsumableInbound> selectExportList(ConsumableInbound inbound);
}
