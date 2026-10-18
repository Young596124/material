package com.material.modules.material.service.impl;

import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.MaterialCodeApplicationDTO;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IMaterialCodeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 物料编码申请 业务实现
 */
@Service
public class MaterialCodeApplicationServiceImpl implements IMaterialCodeApplicationService
{

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
        return today + String.format("%03d", seq);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertApplication(MaterialCodeApplicationDTO dto)
    {
        // 同步到1F线边仓物料基表
        WarehouseMaterialBase base = new WarehouseMaterialBase();
        base.setMaterialCode(generateMaterialCode());
        base.setMaterialName(dto.getMaterialName());
        base.setApplicantId(dto.getApplicantId());
        base.setApplicantName(dto.getApplicantName());
        base.setWarehouseId(dto.getWarehouseId());
        base.setWarehouseName(dto.getWarehouseName());
        base.setProjectSystem(dto.getProjectSystem());
        base.setSourceType(dto.getSourceType());
        base.setLocation(dto.getLocation());
        base.setTotalInbound(0);
        base.setRemainingQuantity(0);
        base.setOutboundQuantity(0);
        base.setAttachment(dto.getAttachment());
        base.setSpecialNote(dto.getSpecialNote());
        base.setCreateBy(SecurityUtils.getNickName());
        base.setCreateTime(new Date());
        base.setUpdateBy(SecurityUtils.getNickName());
        base.setUpdateTime(new Date());
        return baseMapper.insert(base);
    }
}
