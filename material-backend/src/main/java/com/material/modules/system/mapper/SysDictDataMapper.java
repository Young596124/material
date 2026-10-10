package com.material.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.modules.system.domain.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据表 数据层
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData>
{
    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);
}
