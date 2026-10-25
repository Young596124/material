package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.consumable.domain.ConsumableStock;
import com.material.modules.consumable.mapper.ConsumableStockMapper;
import com.material.modules.consumable.service.IConsumableStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 低值易耗品-库存信息 业务实现
 */
@Service
public class ConsumableStockServiceImpl implements IConsumableStockService
{
    @Autowired
    private ConsumableStockMapper stockMapper;

    @Override
    public Page<ConsumableStock> selectList(Page<ConsumableStock> page, ConsumableStock stock)
    {
        return stockMapper.selectPage(page, buildQuery(stock));
    }

    @Override
    public List<ConsumableStock> selectExportList(ConsumableStock stock)
    {
        return stockMapper.selectList(buildQuery(stock));
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<ConsumableStock> buildQuery(ConsumableStock stock)
    {
        return new LambdaQueryWrapper<ConsumableStock>()
                .like(stock.getGoodsName() != null && !"".equals(stock.getGoodsName()),
                        ConsumableStock::getGoodsName, stock.getGoodsName())
                .like(stock.getGoodsCode() != null && !"".equals(stock.getGoodsCode()),
                        ConsumableStock::getGoodsCode, stock.getGoodsCode())
                .like(stock.getCategoryName() != null && !"".equals(stock.getCategoryName()),
                        ConsumableStock::getCategoryName, stock.getCategoryName())
                .eq(stock.getGoodsLevel() != null && !"".equals(stock.getGoodsLevel()),
                        ConsumableStock::getGoodsLevel, stock.getGoodsLevel())
                .eq(stock.getStorehouseId() != null,
                        ConsumableStock::getStorehouseId, stock.getStorehouseId())
                .orderByDesc(ConsumableStock::getId);
    }
}
