package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IWarehouseMaterialBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 1F线边仓物料基表 业务实现
 */
@Service
public class WarehouseMaterialBaseServiceImpl implements IWarehouseMaterialBaseService
{
    @Autowired
    private WarehouseMaterialBaseMapper baseMapper;

    @Override
    public Page<WarehouseMaterialBase> selectList(Page<WarehouseMaterialBase> page, WarehouseMaterialBase base)
    {
        return baseMapper.selectPage(page,
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .like(base.getMaterialCode() != null && !"".equals(base.getMaterialCode()),
                                WarehouseMaterialBase::getMaterialCode, base.getMaterialCode())
                        .like(base.getMaterialName() != null && !"".equals(base.getMaterialName()),
                                WarehouseMaterialBase::getMaterialName, base.getMaterialName())
                        .eq(base.getSourceType() != null && !"".equals(base.getSourceType()),
                                WarehouseMaterialBase::getSourceType, base.getSourceType())
                        .like(base.getLocation() != null && !"".equals(base.getLocation()),
                                WarehouseMaterialBase::getLocation, base.getLocation())
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public WarehouseMaterialBase selectById(Long id)
    {
        return baseMapper.selectById(id);
    }

    @Override
    public List<WarehouseMaterialBase> selectAll()
    {
        return baseMapper.selectList(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public List<WarehouseMaterialBase> selectByMaterialName(String materialName)
    {
        return baseMapper.selectList(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .like(materialName != null && !"".equals(materialName),
                                WarehouseMaterialBase::getMaterialName, materialName)
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public int updateBase(WarehouseMaterialBase base)
    {
        base.setUpdateBy(SecurityUtils.getUsername());
        base.setUpdateTime(new Date());
        return baseMapper.updateById(base);
    }
}
