package com.material.modules.consumable.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.SecurityUtils;
import com.material.modules.consumable.domain.ConsumableStorehouse;
import com.material.modules.consumable.mapper.ConsumableStorehouseMapper;
import com.material.modules.consumable.service.IConsumableStorehouseService;
import com.material.modules.system.domain.SysDept;
import com.material.modules.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 低值易耗品-库房 业务实现
 */
@Service
public class ConsumableStorehouseServiceImpl implements IConsumableStorehouseService
{
    @Autowired
    private ConsumableStorehouseMapper storehouseMapper;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<ConsumableStorehouse> selectList(Page<ConsumableStorehouse> page, ConsumableStorehouse storehouse)
    {
        return storehouseMapper.selectPage(page,
                new LambdaQueryWrapper<ConsumableStorehouse>()
                        .like(storehouse.getStorehouseName() != null && !"".equals(storehouse.getStorehouseName()),
                                ConsumableStorehouse::getStorehouseName, storehouse.getStorehouseName())
                        .like(storehouse.getStorehouseCode() != null && !"".equals(storehouse.getStorehouseCode()),
                                ConsumableStorehouse::getStorehouseCode, storehouse.getStorehouseCode())
                        .eq(storehouse.getStatus() != null && !"".equals(storehouse.getStatus()),
                                ConsumableStorehouse::getStatus, storehouse.getStatus())
                        .orderByDesc(ConsumableStorehouse::getStorehouseId));
    }

    @Override
    public List<ConsumableStorehouse> selectEnabled()
    {
        return storehouseMapper.selectList(
                new LambdaQueryWrapper<ConsumableStorehouse>()
                        .eq(ConsumableStorehouse::getStatus, "0")
                        .orderByAsc(ConsumableStorehouse::getStorehouseId));
    }

    @Override
    public ConsumableStorehouse selectById(Long storehouseId)
    {
        return storehouseMapper.selectById(storehouseId);
    }

    @Override
    public int insertStorehouse(ConsumableStorehouse storehouse)
    {
        // 仓库名称唯一校验
        Long count = storehouseMapper.selectCount(
                new LambdaQueryWrapper<ConsumableStorehouse>()
                        .eq(ConsumableStorehouse::getStorehouseName, storehouse.getStorehouseName()));
        if (count != null && count > 0)
        {
            throw new ServiceException("仓库名称已存在，请勿重复添加");
        }
        // 生成库房编号：KF + 三位流水号
        storehouse.setStorehouseCode(generateStorehouseCode());
        // 根据责任部门ID填充部门名称
        fillDeptName(storehouse);
        storehouse.setCreateBy(SecurityUtils.getUsername());
        storehouse.setCreateTime(new Date());
        if (storehouse.getStatus() == null || "".equals(storehouse.getStatus()))
        {
            storehouse.setStatus("0");
        }
        return storehouseMapper.insert(storehouse);
    }

    @Override
    public int updateStorehouse(ConsumableStorehouse storehouse)
    {
        // 仓库名称唯一校验（排除自身）
        Long count = storehouseMapper.selectCount(
                new LambdaQueryWrapper<ConsumableStorehouse>()
                        .eq(ConsumableStorehouse::getStorehouseName, storehouse.getStorehouseName())
                        .ne(ConsumableStorehouse::getStorehouseId, storehouse.getStorehouseId()));
        if (count != null && count > 0)
        {
            throw new ServiceException("仓库名称已存在，请更换名称");
        }
        fillDeptName(storehouse);
        storehouse.setUpdateBy(SecurityUtils.getUsername());
        storehouse.setUpdateTime(new Date());
        return storehouseMapper.updateById(storehouse);
    }

    @Override
    public int deleteByIds(List<Long> ids)
    {
        return storehouseMapper.deleteBatchIds(ids);
    }

    @Override
    public int changeStatus(ConsumableStorehouse storehouse)
    {
        ConsumableStorehouse update = new ConsumableStorehouse();
        update.setStorehouseId(storehouse.getStorehouseId());
        update.setStatus(storehouse.getStatus());
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(new Date());
        return storehouseMapper.updateById(update);
    }

    /**
     * 生成库房编号：KF + 三位流水号
     */
    private String generateStorehouseCode()
    {
        Long seq = redisTemplate.opsForValue().increment("consumable:storehouse:seq", 1);
        return "KF" + String.format("%03d", seq);
    }

    /**
     * 根据责任部门ID填充部门名称
     */
    private void fillDeptName(ConsumableStorehouse storehouse)
    {
        if (storehouse.getDeptId() != null)
        {
            SysDept dept = deptService.selectDeptById(storehouse.getDeptId());
            if (dept != null)
            {
                storehouse.setDeptName(dept.getDeptName());
            }
        }
        else
        {
            storehouse.setDeptName(null);
        }
    }
}
