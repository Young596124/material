package com.material.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialCodeApplication;

import java.util.List;

/**
 * 物料编码申请 业务层
 */
public interface IMaterialCodeApplicationService
{
    /** 新增（生成物料编码并同步到1F线边仓物料基表） */
    int insertApplication(MaterialCodeApplicationDTO dto);
}
