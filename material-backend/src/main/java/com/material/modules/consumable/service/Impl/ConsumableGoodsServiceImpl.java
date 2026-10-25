package com.material.modules.consumable.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableCategory;
import com.material.modules.consumable.domain.ConsumableGoods;
import com.material.modules.consumable.mapper.ConsumableCategoryMapper;
import com.material.modules.consumable.mapper.ConsumableGoodsMapper;
import com.material.modules.consumable.service.IConsumableGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-物品信息 业务实现
 */
@Service
public class ConsumableGoodsServiceImpl implements IConsumableGoodsService
{
    @Autowired
    private ConsumableGoodsMapper goodsMapper;

    @Autowired
    private ConsumableCategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<ConsumableGoods> selectList(Page<ConsumableGoods> page, ConsumableGoods goods)
    {
        return goodsMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumableGoods>()
                        .eq(goods.getCategoryId() != null,
                                ConsumableGoods::getCategoryId, goods.getCategoryId())
                        .like(goods.getGoodsName() != null && !"".equals(goods.getGoodsName()),
                                ConsumableGoods::getGoodsName, goods.getGoodsName())
                        .like(goods.getGoodsCode() != null && !"".equals(goods.getGoodsCode()),
                                ConsumableGoods::getGoodsCode, goods.getGoodsCode())
                        .eq(goods.getStatus() != null && !"".equals(goods.getStatus()),
                                ConsumableGoods::getStatus, goods.getStatus())
                        .eq(goods.getShowStatus() != null && !"".equals(goods.getShowStatus()),
                                ConsumableGoods::getShowStatus, goods.getShowStatus())
                        .eq(goods.getLevel() != null && !"".equals(goods.getLevel()),
                                ConsumableGoods::getLevel, goods.getLevel())
                        .orderByDesc(ConsumableGoods::getGoodsId));
    }

    @Override
    public ConsumableGoods selectById(Long goodsId)
    {
        return goodsMapper.selectById(goodsId);
    }

    @Override
    public List<ConsumableGoods> selectByCategoryId(Long categoryId)
    {
        return goodsMapper.selectList(
                new LambdaQueryWrapper<ConsumableGoods>()
                        .eq(ConsumableGoods::getCategoryId, categoryId)
                        .eq(ConsumableGoods::getStatus, "0")
                        .eq(ConsumableGoods::getShowStatus, "0")
                        .orderByAsc(ConsumableGoods::getGoodsId));
    }

    @Override
    public int insertGoods(ConsumableGoods goods)
    {
        // 货物名称唯一校验
        Long count = goodsMapper.selectCount(
                new LambdaQueryWrapper<ConsumableGoods>()
                        .eq(ConsumableGoods::getGoodsName, goods.getGoodsName()));
        if (count != null && count > 0)
        {
            throw new ServiceException("货物名称已存在，请勿重复添加");
        }
        // 同步冗余类别名称
        ConsumableCategory category = categoryMapper.selectById(goods.getCategoryId());
        if (category == null)
        {
            throw new ServiceException("所选货物类型不存在");
        }
        goods.setCategoryName(category.getCategoryName());
        // 生成货物编号：大类名称拼音首字母大写 + 六位流水号
        goods.setGoodsCode(generateGoodsCode(category.getCategoryName()));
        goods.setCreateBy(SecurityUtils.getUsername());
        goods.setCreateTime(new Date());
        if (goods.getStatus() == null || "".equals(goods.getStatus()))
        {
            goods.setStatus("0");
        }
        if (goods.getShowStatus() == null || "".equals(goods.getShowStatus()))
        {
            goods.setShowStatus("0");
        }
        if (goods.getConsumeType() == null || "".equals(goods.getConsumeType()))
        {
            goods.setConsumeType("0");
        }
        return goodsMapper.insert(goods);
    }

    @Override
    public int updateGoods(ConsumableGoods goods)
    {
        // 货物名称唯一校验（排除自身）
        Long count = goodsMapper.selectCount(
                new LambdaQueryWrapper<ConsumableGoods>()
                        .eq(ConsumableGoods::getGoodsName, goods.getGoodsName())
                        .ne(ConsumableGoods::getGoodsId, goods.getGoodsId()));
        if (count != null && count > 0)
        {
            throw new ServiceException("货物名称已存在，请更换名称");
        }
        // 同步冗余类别名称
        if (goods.getCategoryId() != null)
        {
            ConsumableCategory category = categoryMapper.selectById(goods.getCategoryId());
            if (category != null)
            {
                goods.setCategoryName(category.getCategoryName());
            }
        }
        goods.setUpdateBy(SecurityUtils.getUsername());
        goods.setUpdateTime(new Date());
        return goodsMapper.updateById(goods);
    }

    @Override
    public int deleteByIds(List<Long> ids)
    {
        return goodsMapper.deleteBatchIds(ids);
    }

    @Override
    public int changeStatus(ConsumableGoods goods)
    {
        ConsumableGoods update = new ConsumableGoods();
        update.setGoodsId(goods.getGoodsId());
        update.setStatus(goods.getStatus());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        return goodsMapper.updateById(update);
    }

    /**
     * 生成货物编号：大类名称拼音首字母大写 + 六位流水号
     */
    private String generateGoodsCode(String categoryName)
    {
        String prefix = PinyinUtil.getFirstLetter(categoryName, "").toUpperCase();
        if (prefix == null || prefix.isEmpty())
        {
            prefix = "SP";
        }
        Long seq = redisTemplate.opsForValue().increment("consumable:goods:seq:" + prefix, 1);
        return prefix + String.format("%06d", seq);
    }
}
