package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.SecurityUtils;
import com.material.modules.system.domain.SysDictType;
import com.material.modules.system.mapper.SysDictTypeMapper;
import com.material.modules.system.service.ISysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 字典类型业务实现
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService
{
    @Autowired
    private SysDictTypeMapper sysDictTypeMapper;

    /**
     * 分页查询字典类型列表
     */
    @Override
    public Page<SysDictType> selectDictTypeList(Page<SysDictType> page, SysDictType dictType)
    {
        return sysDictTypeMapper.selectPage(page,
                new LambdaQueryWrapper<SysDictType>()
                        .like(dictType.getDictName() != null && !"".equals(dictType.getDictName()),
                                SysDictType::getDictName, dictType.getDictName())
                        .like(dictType.getDictType() != null && !"".equals(dictType.getDictType()),
                                SysDictType::getDictType, dictType.getDictType())
                        .eq(dictType.getStatus() != null && !"".equals(dictType.getStatus()),
                                SysDictType::getStatus, dictType.getStatus())
                        .orderByDesc(SysDictType::getDictId));
    }

    /**
     * 根据ID查询字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        return sysDictTypeMapper.selectById(dictId);
    }

    /**
     * 新增字典类型
     */
    @Override
    public int insertDictType(SysDictType dictType)
    {
        dictType.setCreateBy(SecurityUtils.getUsername());
        dictType.setCreateTime(new Date());
        return sysDictTypeMapper.insert(dictType);
    }

    /**
     * 修改字典类型
     */
    @Override
    public int updateDictType(SysDictType dictType)
    {
        dictType.setUpdateBy(SecurityUtils.getUsername());
        dictType.setUpdateTime(new Date());
        return sysDictTypeMapper.updateById(dictType);
    }

    /**
     * 删除字典类型
     */
    @Override
    public int deleteDictType(Long dictId)
    {
        return sysDictTypeMapper.deleteById(dictId);
    }

    /**
     * 批量删除字典类型
     */
    @Override
    public int deleteDictTypeByIds(List<Long> dictIds)
    {
        return sysDictTypeMapper.deleteBatchIds(dictIds);
    }
}
