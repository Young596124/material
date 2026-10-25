package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableGoods;
import com.material.modules.consumable.domain.ConsumableInbound;
import com.material.modules.consumable.domain.ConsumableStock;
import com.material.modules.consumable.domain.ConsumableStorehouse;
import com.material.modules.consumable.mapper.ConsumableGoodsMapper;
import com.material.modules.consumable.mapper.ConsumableInboundMapper;
import com.material.modules.consumable.mapper.ConsumableStockMapper;
import com.material.modules.consumable.mapper.ConsumableStorehouseMapper;
import com.material.modules.consumable.service.IConsumableInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-物品入库 业务实现
 */
@Service
public class ConsumableInboundServiceImpl implements IConsumableInboundService
{
    @Autowired
    private ConsumableInboundMapper inboundMapper;

    @Autowired
    private ConsumableGoodsMapper goodsMapper;

    @Autowired
    private ConsumableStorehouseMapper storehouseMapper;

    @Autowired
    private ConsumableStockMapper stockMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<ConsumableInbound> selectList(Page<ConsumableInbound> page, ConsumableInbound inbound)
    {
        return inboundMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumableInbound>()
                        .like(inbound.getInboundCode() != null && !"".equals(inbound.getInboundCode()),
                                ConsumableInbound::getInboundCode, inbound.getInboundCode())
                        .like(inbound.getGoodsName() != null && !"".equals(inbound.getGoodsName()),
                                ConsumableInbound::getGoodsName, inbound.getGoodsName())
                        .eq(inbound.getCategoryId() != null,
                                ConsumableInbound::getCategoryId, inbound.getCategoryId())
                        .eq(inbound.getStorehouseId() != null,
                                ConsumableInbound::getStorehouseId, inbound.getStorehouseId())
                        .orderByDesc(ConsumableInbound::getCreateTime));
    }

    @Override
    public ConsumableInbound selectById(Long id)
    {
        return inboundMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInbound(ConsumableInbound inbound)
    {
        if (inbound.getGoodsId() == null)
        {
            throw new ServiceException("请选择货物名称");
        }
        if (inbound.getStorehouseId() == null)
        {
            throw new ServiceException("请选择入库库房");
        }
        if (inbound.getQuantity() == null || inbound.getQuantity() <= 0)
        {
            throw new ServiceException("入库总数必须大于0");
        }
        // 冗余货物信息
        ConsumableGoods goods = goodsMapper.selectById(inbound.getGoodsId());
        if (goods == null)
        {
            throw new ServiceException("所选货物不存在");
        }
        inbound.setCategoryId(goods.getCategoryId());
        inbound.setCategoryName(goods.getCategoryName());
        inbound.setGoodsName(goods.getGoodsName());
        inbound.setGoodsCode(goods.getGoodsCode());
        inbound.setUnit(goods.getUnit());
        // 冗余库房信息
        ConsumableStorehouse storehouse = storehouseMapper.selectById(inbound.getStorehouseId());
        if (storehouse == null)
        {
            throw new ServiceException("所选库房不存在");
        }
        inbound.setStorehouseName(storehouse.getStorehouseName());
        inbound.setStorehouseCode(storehouse.getStorehouseCode());
        inbound.setStorehouseAddress(storehouse.getAddress());
        inbound.setDeptName(storehouse.getDeptName());
        // 生成入库编号
        inbound.setInboundCode(generateInboundCode());
        if (inbound.getInboundTime() == null)
        {
            inbound.setInboundTime(new Date());
        }
        inbound.setCreateBy(SecurityUtils.getUsername());
        inbound.setCreateTime(new Date());
        int rows = inboundMapper.insert(inbound);
        // 更新库存
        addStock(goods, storehouse, inbound.getQuantity());
        return rows;
    }

    @Override
    public List<ConsumableInbound> selectExportList(ConsumableInbound inbound)
    {
        return inboundMapper.selectList(
                new LambdaQueryWrapper<ConsumableInbound>()
                        .like(inbound.getInboundCode() != null && !"".equals(inbound.getInboundCode()),
                                ConsumableInbound::getInboundCode, inbound.getInboundCode())
                        .like(inbound.getGoodsName() != null && !"".equals(inbound.getGoodsName()),
                                ConsumableInbound::getGoodsName, inbound.getGoodsName())
                        .eq(inbound.getCategoryId() != null,
                                ConsumableInbound::getCategoryId, inbound.getCategoryId())
                        .eq(inbound.getStorehouseId() != null,
                                ConsumableInbound::getStorehouseId, inbound.getStorehouseId())
                        .orderByDesc(ConsumableInbound::getCreateTime));
    }

    /**
     * 入库后累加库存：库房ID+物品ID唯一，存在则叠加，不存在则新建
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
            stock.setInboundQuantity((stock.getInboundQuantity() == null ? 0 : stock.getInboundQuantity()) + quantity);
            stock.setUpdateBy(SecurityUtils.getUsername());
            stock.setUpdateTime(new Date());
            stockMapper.updateById(stock);
        }
        else
        {
            ConsumableStock newStock = buildNewStock(goods, storehouse);
            newStock.setStockQuantity(quantity);
            newStock.setInboundQuantity(quantity);
            stockMapper.insert(newStock);
        }
    }

    /**
     * 构建新的库存记录（冗余货物与库房信息）
     */
    private ConsumableStock buildNewStock(ConsumableGoods goods, ConsumableStorehouse storehouse)
    {
        ConsumableStock stock = new ConsumableStock();
        stock.setStorehouseId(storehouse.getStorehouseId());
        stock.setGoodsId(goods.getGoodsId());
        stock.setCategoryName(goods.getCategoryName());
        stock.setGoodsLevel(goods.getLevel());
        stock.setGoodsName(goods.getGoodsName());
        stock.setGoodsCode(goods.getGoodsCode());
        stock.setGoodsImage(goods.getImage());
        stock.setStockQuantity(0);
        stock.setInboundQuantity(0);
        stock.setUsedQuantity(0);
        stock.setReturnQuantity(0);
        stock.setUnit(goods.getUnit());
        stock.setStorehouseName(storehouse.getStorehouseName());
        stock.setStorehouseCode(storehouse.getStorehouseCode());
        stock.setStorehouseAddress(storehouse.getAddress());
        stock.setManager(storehouse.getDeptName());
        stock.setCreateBy(SecurityUtils.getUsername());
        stock.setCreateTime(new Date());
        return stock;
    }

    /**
     * 生成入库编号：RK + yyyyMMdd + 3位流水号
     */
    private String generateInboundCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("consumable:inbound:seq:" + today, 1);
        return "RK" + today + String.format("%03d", seq);
    }
}
