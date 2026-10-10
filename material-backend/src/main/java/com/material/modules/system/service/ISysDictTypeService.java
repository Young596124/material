package com.material.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.SysDictType;

import java.util.List;

/**
 * 字典类型业务层
 */
public interface ISysDictTypeService
{
    /**
     * 分页查询字典类型列表
     */
    Page<SysDictType> selectDictTypeList(Page<SysDictType> page, SysDictType dictType);

    /**
     * 根据ID查询字典类型
     */
    SysDictType selectDictTypeById(Long dictId);

    /**
     * 新增字典类型
     */
    int insertDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    int updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    int deleteDictType(Long dictId);

    /**
     * 批量删除字典类型
     */
    int deleteDictTypeByIds(List<Long> dictIds);
}
