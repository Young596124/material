package com.material.modules.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.modules.material.domain.MaterialCodeApplication;

import java.util.List;

/**
 * 物料编码申请 业务层
 */
public interface IMaterialCodeApplicationService
{
    /** 分页查询列表 */
    Page<MaterialCodeApplication> selectList(Page<MaterialCodeApplication> page, MaterialCodeApplication application);

    /** 根据ID查询 */
    MaterialCodeApplication selectById(Long id);

    /** 新增（生成物料编码并同步到1F线边仓物料基表） */
    int insertApplication(MaterialCodeApplication application);

    /** 修改 */
    int updateApplication(MaterialCodeApplication application);

    /** 删除 */
    int deleteByIds(List<Long> ids);
}
