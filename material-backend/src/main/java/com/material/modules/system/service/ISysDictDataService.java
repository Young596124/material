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
}
