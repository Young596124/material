package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.MaterialCodeApplication;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.MaterialCodeApplicationMapper;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IMaterialCodeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 物料编码申请 业务实现
 */
@Service
public class MaterialCodeApplicationServiceImpl implements IMaterialCodeApplicationService
{
    @Autowired
    private MaterialCodeApplicationMapper applicationMapper;

    @Autowired
    private WarehouseMaterialBaseMapper baseMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成物料编码：yyyyMMdd + 2位流水号
     */
    private String generateMaterialCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("material:code:seq:" + today, 1);
        return today + String.format("%02d", seq);
    }

    @Override
    public Page<MaterialCodeApplication> selectList(Page<MaterialCodeApplication> page, MaterialCodeApplication application)
    {
        return applicationMapper.selectPage(page,
                new LambdaQueryWrapper<MaterialCodeApplication>()
                        .like(application.getMaterialCode() != null && !"".equals(application.getMaterialCode()),
                                MaterialCodeApplication::getMaterialCode, application.getMaterialCode())
                        .like(application.getMaterialName() != null && !"".equals(application.getMaterialName()),
                                MaterialCodeApplication::getMaterialName, application.getMaterialName())
                        .like(application.getApplicantName() != null && !"".equals(application.getApplicantName()),
                                MaterialCodeApplication::getApplicantName, application.getApplicantName())
                        .eq(application.getSourceType() != null && !"".equals(application.getSourceType()),
                                MaterialCodeApplication::getSourceType, application.getSourceType())
                        .orderByDesc(MaterialCodeApplication::getId));
    }

    @Override
    public MaterialCodeApplication selectById(Long id)
    {
        return applicationMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertApplication(MaterialCodeApplication application)
    {
        // 生成物料编码
        application.setMaterialCode(generateMaterialCode());
        application.setCreateBy(SecurityUtils.getUsername());
        application.setCreateTime(new Date());
        int rows = applicationMapper.insert(application);

        // 同步到1F线边仓物料基表
        WarehouseMaterialBase base = new WarehouseMaterialBase();
        base.setApplicationId(application.getId());
        base.setMaterialCode(application.getMaterialCode());
        base.setMaterialName(application.getMaterialName());
        base.setProjectSystem(application.getProjectSystem());
        base.setSourceType(application.getSourceType());
        base.setLocation(application.getLocation());
        base.setTotalInbound(0);
        base.setRemainingQuantity(0);
        base.setOutboundQuantity(0);
        base.setCreateBy(SecurityUtils.getUsername());
        base.setCreateTime(new Date());
        baseMapper.insert(base);
        return rows;
    }

    @Override
    public int updateApplication(MaterialCodeApplication application)
    {
        application.setUpdateBy(SecurityUtils.getUsername());
        application.setUpdateTime(new Date());
        int rows = applicationMapper.updateById(application);

        // 同步更新物料基表
        WarehouseMaterialBase base = baseMapper.selectOne(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .eq(WarehouseMaterialBase::getApplicationId, application.getId()));
        if (base != null)
        {
            base.setMaterialName(application.getMaterialName());
            base.setProjectSystem(application.getProjectSystem());
            base.setSourceType(application.getSourceType());
            base.setLocation(application.getLocation());
            base.setUpdateBy(SecurityUtils.getUsername());
            base.setUpdateTime(new Date());
            baseMapper.updateById(base);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Long> ids)
    {
        return applicationMapper.deleteBatchIds(ids);
    }
}
