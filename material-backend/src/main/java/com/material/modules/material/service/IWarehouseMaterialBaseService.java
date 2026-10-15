package com.material.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.WarehouseMaterialBase;

import java.util.List;

/**
 * 1F线边仓物料基表 业务层
 */
public interface IWarehouseMaterialBaseService
{
    /** 分页查询列表 */
    Page<WarehouseMaterialBase> selectList(Page<WarehouseMaterialBase> page, WarehouseMaterialBase base);

    /** 根据ID查询 */
    WarehouseMaterialBase selectById(Long id);

    /** 查询全部物料（供入库/出库下拉选择） */
    List<WarehouseMaterialBase> selectAll();

    /** 根据物料名称模糊查询（供入库/出库选择物料） */
    List<WarehouseMaterialBase> selectByMaterialName(String materialName);

    /** 修改 */
    int updateBase(WarehouseMaterialBase base);

    /** 批量导入（物料编码存在则更新，否则新增） */
    int importBatch(List<WarehouseMaterialBase> list);

    /** 将列表中来源类型字典值转换为标签（导出时使用） */
    List<WarehouseMaterialBase> convertSourceTypeValueToLabel(List<WarehouseMaterialBase> list);
}
