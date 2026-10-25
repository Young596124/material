package com.material.modules.consumable.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableCategory;

import java.util.List;

/**
 * 低值易耗品-物品类别 业务层
 */
public interface IConsumableCategoryService
{
    /** 分页查询类别列表 */
    Page<ConsumableCategory> selectList(Page<ConsumableCategory> page, ConsumableCategory category);

    /** 查询全部启用的类别（供下拉选择） */
    List<ConsumableCategory> selectEnabled();

    /** 根据ID查询 */
    ConsumableCategory selectById(Long categoryId);

    /** 新增类别 */
    int insertCategory(ConsumableCategory category);

    /** 修改类别 */
    int updateCategory(ConsumableCategory category);

    /** 批量删除类别 */
    int deleteByIds(List<Long> ids);

    /** 修改状态 */
    int changeStatus(ConsumableCategory category);
}
