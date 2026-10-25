package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableGoods;
import com.material.modules.consumable.domain.ConsumableReturn;
import com.material.modules.consumable.domain.ConsumableStock;
import com.material.modules.consumable.domain.ConsumableStorehouse;
import com.material.modules.consumable.mapper.ConsumableGoodsMapper;
import com.material.modules.consumable.mapper.ConsumableReturnMapper;
import com.material.modules.consumable.mapper.ConsumableStockMapper;
import com.material.modules.consumable.mapper.ConsumableStorehouseMapper;
import com.material.modules.consumable.service.IConsumableReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-物品退库 业务实现
 */
@Service
public class ConsumableReturnServiceImpl implements IConsumableReturnService
{
    @Autowired
    private ConsumableReturnMapper returnMapper;

    @Autowired
    private ConsumableGoodsMapper goodsMapper;

    @Autowired
    private ConsumableStorehouseMapper storehouseMapper;

    @Autowired
    private ConsumableStockMapper stockMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<ConsumableReturn> selectList(Page<ConsumableReturn> page, ConsumableReturn returnRecord)
    {
        return returnMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumableReturn>()
                        .like(returnRecord.getReturnCode() != null && !"".equals(returnRecord.getReturnCode()),
                                ConsumableReturn::getReturnCode, returnRecord.getReturnCode())
                        .like(returnRecord.getGoodsName() != null && !"".equals(returnRecord.getGoodsName()),
                                ConsumableReturn::getGoodsName, returnRecord.getGoodsName())
                        .eq(returnRecord.getCategoryId() != null,
                                ConsumableReturn::getCategoryId, returnRecord.getCategoryId())
                        .eq(returnRecord.getStorehouseId() != null,
                                ConsumableReturn::getStorehouseId, returnRecord.getStorehouseId())
                        .orderByDesc(ConsumableReturn::getCreateTime));
    }

    @Override
    public ConsumableReturn selectById(Long id)
    {
        return returnMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReturn(ConsumableReturn returnRecord)
    {
        if (returnRecord.getGoodsId() == null)
        {
            throw new ServiceException("请选择货物名称");
        }
        if (returnRecord.getStorehouseId() == null)
        {
            throw new ServiceException("请选择退库库房");
        }
        if (returnRecord.getQuantity() == null || returnRecord.getQuantity() <= 0)
        {
            throw new ServiceException("退库总数必须大于0");
        }
        // 冗余货物信息
        ConsumableGoods goods = goodsMapper.selectById(returnRecord.getGoodsId());
        if (goods == null)
        {
            throw new ServiceException("所选货物不存在");
        }
        returnRecord.setCategoryId(goods.getCategoryId());
        returnRecord.setCategoryName(goods.getCategoryName());
        returnRecord.setGoodsName(goods.getGoodsName());
        returnRecord.setGoodsCode(goods.getGoodsCode());
        returnRecord.setUnit(goods.getUnit());
        // 冗余库房信息
        ConsumableStorehouse storehouse = storehouseMapper.selectById(returnRecord.getStorehouseId());
        if (storehouse == null)
        {
            throw new ServiceException("所选库房不存在");
        }
        returnRecord.setStorehouseName(storehouse.getStorehouseName());
        returnRecord.setStorehouseCode(storehouse.getStorehouseCode());
        returnRecord.setStorehouseAddress(storehouse.getAddress());
        returnRecord.setDeptName(storehouse.getDeptName());
        // 回退库存
        addStock(goods, storehouse, returnRecord.getQuantity());
        // 生成退库编号
        returnRecord.setReturnCode(generateReturnCode());
        if (returnRecord.getReturnTime() == null)
        {
            returnRecord.setReturnTime(new Date());
        }
        returnRecord.setCreateBy(SecurityUtils.getUsername());
        returnRecord.setCreateTime(new Date());
        return returnMapper.insert(returnRecord);
    }

    @Override
    public List<ConsumableReturn> selectExportList(ConsumableReturn returnRecord)
    {
        return returnMapper.selectList(
                new LambdaQueryWrapper<ConsumableReturn>()
                        .like(returnRecord.getReturnCode() != null && !"".equals(returnRecord.getReturnCode()),
                                ConsumableReturn::getReturnCode, returnRecord.getReturnCode())
                        .like(returnRecord.getGoodsName() != null && !"".equals(returnRecord.getGoodsName()),
                                ConsumableReturn::getGoodsName, returnRecord.getGoodsName())
                        .eq(returnRecord.getCategoryId() != null,
                                ConsumableReturn::getCategoryId, returnRecord.getCategoryId())
                        .eq(returnRecord.getStorehouseId() != null,
                                ConsumableReturn::getStorehouseId, returnRecord.getStorehouseId())
                        .orderByDesc(ConsumableReturn::getCreateTime));
    }

    /**
     * 退库后回退库存：存在则叠加，不存在则新建
     */
    private void addStock(ConsumableGoods goods, ConsumableStorehouse storehouse, Integer quantity)
    {
        ConsumableStock stock = stockMapper.selectOne(
                new LambdaQueryWrapper<ConsumableStock>()
                        .eq(ConsumableStock::getStorehouseId, storehouse.getStorehouseId())
                        .eq(ConsumableStock::getGoodsId, goods.getGoodsId())
                        .last("limit 1"));
        if (stock != null)
        {
            stock.setStockQuantity((stock.getStockQuantity() == null ? 0 : stock.getStockQuantity()) + quantity);
            stock.setReturnQuantity((stock.getReturnQuantity() == null ? 0 : stock.getReturnQuantity()) + quantity);
            stock.setUpdateBy(SecurityUtils.getUsername());
            stock.setUpdateTime(new Date());
            stockMapper.updateById(stock);
        }
        else
        {
            ConsumableStock newStock = new ConsumableStock();
            newStock.setStorehouseId(storehouse.getStorehouseId());
            newStock.setGoodsId(goods.getGoodsId());
            newStock.setCategoryName(goods.getCategoryName());
            newStock.setGoodsLevel(goods.getLevel());
            newStock.setGoodsName(goods.getGoodsName());
            newStock.setGoodsCode(goods.getGoodsCode());
            newStock.setGoodsImage(goods.getImage());
            newStock.setStockQuantity(quantity);
            newStock.setInboundQuantity(0);
            newStock.setUsedQuantity(0);
            newStock.setReturnQuantity(quantity);
            newStock.setUnit(goods.getUnit());
            newStock.setStorehouseName(storehouse.getStorehouseName());
            newStock.setStorehouseCode(storehouse.getStorehouseCode());
            newStock.setStorehouseAddress(storehouse.getAddress());
            newStock.setManager(storehouse.getDeptName());
            newStock.setCreateBy(SecurityUtils.getUsername());
            newStock.setCreateTime(new Date());
            stockMapper.insert(newStock);
        }
    }

    /**
     * 生成退库编号：TK + yyyyMMdd + 3位流水号
     */
    private String generateReturnCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("consumable:return:seq:" + today, 1);
        return "TK" + today + String.format("%03d", seq);
    }
}
