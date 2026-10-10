package com.material.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialOutbound;
import com.material.modules.material.domain.MaterialOutboundDetail;
import com.material.modules.material.domain.OutboundDTO;

import java.util.List;

/**
 * 物料出库 业务层
 */
public interface IMaterialOutboundService
{
    /** 提交出库表单（生成编号、保存主子表、更新库存） */
    int insertOutbound(OutboundDTO dto);

    /** 分页查询出库记录 */
    Page<MaterialOutbound> selectRecordList(Page<MaterialOutbound> page, MaterialOutbound outbound);

    /** 根据出库ID查询记录（含明细） */
    MaterialOutbound selectRecordById(Long id);

    /** 查询出库明细列表（记录展开） */
    List<MaterialOutboundDetail> selectDetailListByOutboundId(Long outboundId);

    /** 分页查询出库数据管理 */
    Page<MaterialOutboundDetail> selectDataList(Page<MaterialOutboundDetail> page, MaterialOutboundDetail detail);
}
