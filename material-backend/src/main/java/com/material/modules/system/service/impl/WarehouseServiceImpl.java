package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.system.domain.Warehouse;
import com.material.modules.system.mapper.WarehouseMapper;
import com.material.modules.system.service.IWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 仓库 业务实现
 */
@Service
public class WarehouseServiceImpl implements IWarehouseService
{
    @Autowired
    private WarehouseMapper warehouseMapper;

    @Override
    public Page<Warehouse> selectList(Page<Warehouse> page, Warehouse warehouse)
    {
        return warehouseMapper.selectPage(page,
                new LambdaQueryWrapper<Warehouse>()
                        .like(warehouse.getName() != null && !"".equals(warehouse.getName()),
                                Warehouse::getName, warehouse.getName())
                        .like(warehouse.getManager() != null && !"".equals(warehouse.getManager()),
                                Warehouse::getManager, warehouse.getManager())
                        .eq(warehouse.getStatus() != null && !"".equals(warehouse.getStatus()),
                                Warehouse::getStatus, warehouse.getStatus())
                        .orderByDesc(Warehouse::getId));
    }

    @Override
    public Warehouse selectById(Long id)
    {
        return warehouseMapper.selectById(id);
    }

    @Override
    public List<Warehouse> selectEnabled()
    {
        return warehouseMapper.selectList(
                new LambdaQueryWrapper<Warehouse>()
                        .eq(Warehouse::getStatus, "0")
                        .orderByAsc(Warehouse::getId));
    }

    @Override
    public Warehouse selectByName(String name)
    {
        return warehouseMapper.selectOne(
                new LambdaQueryWrapper<Warehouse>()
                        .eq(Warehouse::getName, name)
                        .last("limit 1"));
    }

    @Override
    public int insertWarehouse(Warehouse warehouse)
    {
        warehouse.setCreateBy(SecurityUtils.getUsername());
        warehouse.setCreateTime(new Date());
        if (warehouse.getStatus() == null || "".equals(warehouse.getStatus()))
        {
            warehouse.setStatus("0");
        }
        return warehouseMapper.insert(warehouse);
    }

    @Override
    public int updateWarehouse(Warehouse warehouse)
    {
        warehouse.setUpdateBy(SecurityUtils.getUsername());
        warehouse.setUpdateTime(new Date());
        return warehouseMapper.updateById(warehouse);
    }

    @Override
    public int deleteByIds(List<Long> ids)
    {
        return warehouseMapper.deleteBatchIds(ids);
    }
}
