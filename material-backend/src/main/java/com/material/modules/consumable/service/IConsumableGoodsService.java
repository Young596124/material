package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableGoods;

import java.util.List;

/**
 * 低值易耗品-物品信息 业务层
 */
public interface IConsumableGoodsService
{
    /** 分页查询物品列表 */
    Page<ConsumableGoods> selectList(Page<ConsumableGoods> page, ConsumableGoods goods);

    /** 根据ID查询 */
    ConsumableGoods selectById(Long goodsId);

    /** 根据类别ID查询启用且显示的物品（供联动下拉） */
    List<ConsumableGoods> selectByCategoryId(Long categoryId);

    /** 新增物品 */
    int insertGoods(ConsumableGoods goods);

    /** 修改物品 */
    int updateGoods(ConsumableGoods goods);

    /** 批量删除物品 */
    int deleteByIds(List<Long> ids);

    /** 修改状态 */
    int changeStatus(ConsumableGoods goods);
}
