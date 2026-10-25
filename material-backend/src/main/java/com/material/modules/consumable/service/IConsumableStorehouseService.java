package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableStorehouse;

import java.util.List;

/**
 * 低值易耗品-库房 业务层
 */
public interface IConsumableStorehouseService
{
    /** 分页查询库房列表 */
    Page<ConsumableStorehouse> selectList(Page<ConsumableStorehouse> page, ConsumableStorehouse storehouse);

    /** 查询全部启用的库房（供弹框选择） */
    List<ConsumableStorehouse> selectEnabled();

    /** 根据ID查询 */
    ConsumableStorehouse selectById(Long storehouseId);

    /** 新增库房 */
    int insertStorehouse(ConsumableStorehouse storehouse);

    /** 修改库房 */
    int updateStorehouse(ConsumableStorehouse storehouse);

    /** 批量删除库房 */
    int deleteByIds(List<Long> ids);

    /** 修改状态 */
    int changeStatus(ConsumableStorehouse storehouse);
}
