package com.material.modules.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.system.domain.Warehouse;

import java.util.List;

/**
 * 仓库 业务层
 */
public interface IWarehouseService
{
    /** 分页查询仓库列表 */
    Page<Warehouse> selectList(Page<Warehouse> page, Warehouse warehouse);

    /** 根据ID查询仓库 */
    Warehouse selectById(Long id);

    /** 查询全部启用的仓库（供下拉选择） */
    List<Warehouse> selectEnabled();

    /** 按仓库名称查询 */
    Warehouse selectByName(String name);

    /** 新增仓库 */
    int insertWarehouse(Warehouse warehouse);

    /** 修改仓库 */
    int updateWarehouse(Warehouse warehouse);

    /** 批量删除仓库 */
    int deleteByIds(List<Long> ids);
}
