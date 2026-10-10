package com.material.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.InboundDTO;
import com.material.modules.material.domain.MaterialInbound;
import com.material.modules.material.domain.MaterialInboundDetail;

import java.util.List;

/**
 * 物料入库 业务层
 */
public interface IMaterialInboundService
{
    /** 提交入库表单（生成编号、保存主子表、更新库存） */
    int insertInbound(InboundDTO dto);

    /** 分页查询入库记录 */
    Page<MaterialInbound> selectRecordList(Page<MaterialInbound> page, MaterialInbound inbound);

    /** 根据入库ID查询记录（含明细） */
    MaterialInbound selectRecordById(Long id);

    /** 查询入库明细列表（记录展开） */
    List<MaterialInboundDetail> selectDetailListByInboundId(Long inboundId);

    /** 分页查询入库数据管理 */
    Page<MaterialInboundDetail> selectDataList(Page<MaterialInboundDetail> page, MaterialInboundDetail detail);
}
