package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableCategory;
import com.material.modules.consumable.mapper.ConsumableCategoryMapper;
import com.material.modules.consumable.service.IConsumableCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-物品类别 业务实现
 */
@Service
public class ConsumableCategoryServiceImpl implements IConsumableCategoryService
{
    @Autowired
    private ConsumableCategoryMapper categoryMapper;

    @Override
    public Page<ConsumableCategory> selectList(Page<ConsumableCategory> page, ConsumableCategory category)
    {
        return categoryMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumableCategory>()
                        .like(category.getCategoryName() != null && !"".equals(category.getCategoryName()),
                                ConsumableCategory::getCategoryName, category.getCategoryName())
                        .eq(category.getStatus() != null && !"".equals(category.getStatus()),
                                ConsumableCategory::getStatus, category.getStatus())
                        .orderByAsc(ConsumableCategory::getOrderNum)
                        .orderByAsc(ConsumableCategory::getCategoryId));
    }

    @Override
    public List<ConsumableCategory> selectEnabled()
    {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<ConsumableCategory>()
                        .eq(ConsumableCategory::getStatus, "0")
                        .orderByAsc(ConsumableCategory::getOrderNum)
                        .orderByAsc(ConsumableCategory::getCategoryId));
    }

    @Override
    public ConsumableCategory selectById(Long categoryId)
    {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public int insertCategory(ConsumableCategory category)
    {
        category.setCreateBy(SecurityUtils.getUsername());
        category.setCreateTime(new Date());
        if (category.getStatus() == null || "".equals(category.getStatus()))
        {
            category.setStatus("0");
        }
        if (category.getOrderNum() == null)
        {
            category.setOrderNum(0);
        }
        return categoryMapper.insert(category);
    }

    @Override
    public int updateCategory(ConsumableCategory category)
    {
        category.setUpdateBy(SecurityUtils.getUsername());
        category.setUpdateTime(new Date());
        return categoryMapper.updateById(category);
    }

    @Override
    public int deleteByIds(List<Long> ids)
    {
        return categoryMapper.deleteBatchIds(ids);
    }

    @Override
    public int changeStatus(ConsumableCategory category)
    {
        ConsumableCategory update = new ConsumableCategory();
        update.setCategoryId(category.getCategoryId());
        update.setStatus(category.getStatus());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        return categoryMapper.updateById(update);
    }
}
