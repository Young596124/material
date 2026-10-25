package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableGoods;
import com.material.modules.consumable.domain.ConsumablePick;
import com.material.modules.consumable.domain.ConsumableStock;
import com.material.modules.consumable.domain.ConsumableStorehouse;
import com.material.modules.consumable.mapper.ConsumableGoodsMapper;
import com.material.modules.consumable.mapper.ConsumablePickMapper;
import com.material.modules.consumable.mapper.ConsumableStockMapper;
import com.material.modules.consumable.mapper.ConsumableStorehouseMapper;
import com.material.modules.consumable.service.IConsumablePickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-物品领用 业务实现
 */
@Service
public class ConsumablePickServiceImpl implements IConsumablePickService
{
    @Autowired
    private ConsumablePickMapper pickMapper;

    @Autowired
    private ConsumableGoodsMapper goodsMapper;

    @Autowired
    private ConsumableStorehouseMapper storehouseMapper;

    @Autowired
    private ConsumableStockMapper stockMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<ConsumablePick> selectList(Page<ConsumablePick> page, ConsumablePick pick)
    {
        return pickMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumablePick>()
                        .like(pick.getPickCode() != null && !"".equals(pick.getPickCode()),
                                ConsumablePick::getPickCode, pick.getPickCode())
                        .like(pick.getGoodsName() != null && !"".equals(pick.getGoodsName()),
                                ConsumablePick::getGoodsName, pick.getGoodsName())
                        .eq(pick.getCategoryId() != null,
                                ConsumablePick::getCategoryId, pick.getCategoryId())
                        .eq(pick.getStorehouseId() != null,
                                ConsumablePick::getStorehouseId, pick.getStorehouseId())
                        .orderByDesc(ConsumablePick::getCreateTime));
    }

    @Override
    public ConsumablePick selectById(Long id)
    {
        return pickMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPick(ConsumablePick pick)
    {
        if (pick.getGoodsId() == null)
        {
            throw new ServiceException("请选择货物名称");
        }
        if (pick.getStorehouseId() == null)
        {
            throw new ServiceException("请选择领用库房");
        }
        if (pick.getQuantity() == null || pick.getQuantity() <= 0)
        {
            throw new ServiceException("领用总数必须大于0");
        }
        // 冗余货物信息
        ConsumableGoods goods = goodsMapper.selectById(pick.getGoodsId());
        if (goods == null)
        {
            throw new ServiceException("所选货物不存在");
        }
        pick.setCategoryId(goods.getCategoryId());
        pick.setCategoryName(goods.getCategoryName());
        pick.setGoodsName(goods.getGoodsName());
        pick.setGoodsCode(goods.getGoodsCode());
        pick.setUnit(goods.getUnit());
        // 冗余库房信息
        ConsumableStorehouse storehouse = storehouseMapper.selectById(pick.getStorehouseId());
        if (storehouse == null)
        {
            throw new ServiceException("所选库房不存在");
        }
        pick.setStorehouseName(storehouse.getStorehouseName());
        pick.setStorehouseCode(storehouse.getStorehouseCode());
        pick.setStorehouseAddress(storehouse.getAddress());
        pick.setDeptName(storehouse.getDeptName());
        // 扣减库存
        deductStock(pick.getStorehouseId(), pick.getGoodsId(), pick.getQuantity());
        // 生成领用编号
        pick.setPickCode(generatePickCode());
        if (pick.getPickTime() == null)
        {
            pick.setPickTime(new Date());
        }
        pick.setCreateBy(SecurityUtils.getUsername());
        pick.setCreateTime(new Date());
        return pickMapper.insert(pick);
    }

    @Override
    public List<ConsumablePick> selectExportList(ConsumablePick pick)
    {
        return pickMapper.selectList(
                new LambdaQueryWrapper<ConsumablePick>()
                        .like(pick.getPickCode() != null && !"".equals(pick.getPickCode()),
                                ConsumablePick::getPickCode, pick.getPickCode())
                        .like(pick.getGoodsName() != null && !"".equals(pick.getGoodsName()),
                                ConsumablePick::getGoodsName, pick.getGoodsName())
                        .eq(pick.getCategoryId() != null,
                                ConsumablePick::getCategoryId, pick.getCategoryId())
                        .eq(pick.getStorehouseId() != null,
                                ConsumablePick::getStorehouseId, pick.getStorehouseId())
                        .orderByDesc(ConsumablePick::getCreateTime));
    }

    /**
     * 领用后扣减库存：库存必须存在且库存数量充足
     */
    private void deductStock(Long storehouseId, Long goodsId, Integer quantity)
    {
        ConsumableStock stock = stockMapper.selectOne(
                new LambdaQueryWrapper<ConsumableStock>()
                        .eq(ConsumableStock::getStorehouseId, storehouseId)
                        .eq(ConsumableStock::getGoodsId, goodsId)
                        .last("limit 1"));
        if (stock == null)
        {
            throw new ServiceException("该库房下无此货物库存，无法领用");
        }
        int stockQty = stock.getStockQuantity() == null ? 0 : stock.getStockQuantity();
        if (stockQty < quantity)
        {
            throw new ServiceException("库存不足，当前库存：" + stockQty);
        }
        stock.setStockQuantity(stockQty - quantity);
        stock.setUsedQuantity((stock.getUsedQuantity() == null ? 0 : stock.getUsedQuantity()) + quantity);
        stock.setUpdateBy(SecurityUtils.getUsername());
        stock.setUpdateTime(new Date());
        stockMapper.updateById(stock);
    }

    /**
     * 生成领用编号：LY + yyyyMMdd + 3位流水号
     */
    private String generatePickCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("consumable:pick:seq:" + today, 1);
        return "LY" + today + String.format("%03d", seq);
    }
}
