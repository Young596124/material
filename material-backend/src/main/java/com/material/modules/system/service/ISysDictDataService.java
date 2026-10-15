package com.material.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.SysDictData;

import java.util.List;

/**
 * 字典数据业务层
 */
public interface ISysDictDataService
{
    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(String dictType);

    /**
     * 分页查询字典数据列表
     */
    Page<SysDictData> selectDictDataList(Page<SysDictData> page, SysDictData dictData);

    /**
     * 根据 dictCode 查询
     */
    SysDictData selectDictDataById(Long dictCode);

    /**
     * 新增
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改
     */
    int updateDictData(SysDictData dictData);

    /**
     * 删除(批量)
     */
    int deleteDictDataByIds(List<Long> dictCodes);
}
