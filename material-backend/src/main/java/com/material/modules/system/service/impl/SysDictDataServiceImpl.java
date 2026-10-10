package com.material.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.SysDictData;
import com.material.modules.system.mapper.SysDictDataMapper;
import com.material.modules.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据业务实现
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService
{
    @Autowired
    private SysDictDataMapper sysDictDataMapper;

    /**
     * 根据字典类型查询字典数据
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType)
    {
        return sysDictDataMapper.selectDictDataByType(dictType);
    }

    /**
     * 分页查询字典数据列表
     */
    @Override
    public Page<SysDictData> selectDictDataList(Page<SysDictData> page, SysDictData dictData)
    {
        return sysDictDataMapper.selectPage(page,
                new LambdaQueryWrapper<SysDictData>()
                        .like(dictData.getDictType() != null && !"".equals(dictData.getDictType()),
                                SysDictData::getDictType, dictData.getDictType())
                        .like(dictData.getDictLabel() != null && !"".equals(dictData.getDictLabel()),
                                SysDictData::getDictLabel, dictData.getDictLabel())
                        .eq(dictData.getStatus() != null && !"".equals(dictData.getStatus()),
                                SysDictData::getStatus, dictData.getStatus())
                        .orderByAsc(SysDictData::getDictSort));
    }
}
 
