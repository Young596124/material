package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.InboundDTO;
import com.material.modules.material.domain.MaterialInbound;
import com.material.modules.material.domain.MaterialInboundDetail;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.MaterialInboundDetailMapper;
import com.material.modules.material.mapper.MaterialInboundMapper;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IMaterialInboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 物料入库 业务实现
 */
@Service
public class MaterialInboundServiceImpl implements IMaterialInboundService
{
    @Autowired
    private MaterialInboundMapper inboundMapper;

    @Autowired
    private MaterialInboundDetailMapper detailMapper;

    @Autowired
    private WarehouseMaterialBaseMapper baseMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成入库编号：RK + yyyyMMdd + 2位流水号
     */
    private String generateInboundCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("material:inbound:seq:" + today, 1);
        return "RK" + today + String.format("%02d", seq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInbound(InboundDTO dto)
    {
        // 保存入库主表
        MaterialInbound inbound = new MaterialInbound();
        inbound.setInboundCode(generateInboundCode());
        inbound.setUserId(dto.getUserId());
        inbound.setUserName(dto.getUserName());
        inbound.setInboundDate(dto.getInboundDate() == null ? new Date() : dto.getInboundDate());
        inbound.setDescription(dto.getDescription());
        inbound.setCreateBy(SecurityUtils.getUsername());
        inbound.setCreateTime(new Date());
        inboundMapper.insert(inbound);

        // 保存子表并更新库存
        if (dto.getDetails() != null)
        {
            for (MaterialInboundDetail detail : dto.getDetails())
            {
                detail.setInboundId(inbound.getId());
                detail.setCreateBy(SecurityUtils.getUsername());
                detail.setCreateTime(new Date());
                detailMapper.insert(detail);

                // 状态=入库 才更新基表库存
                if ("1".equals(detail.getStatus()) && detail.getMaterialBaseId() != null)
                {
                    WarehouseMaterialBase base = baseMapper.selectById(detail.getMaterialBaseId());
                    if (base != null)
                    {
                        int qty = detail.getInboundQuantity() == null ? 0 : detail.getInboundQuantity();
                        base.setTotalInbound((base.getTotalInbound() == null ? 0 : base.getTotalInbound()) + qty);
                        base.setRemainingQuantity((base.getRemainingQuantity() == null ? 0 : base.getRemainingQuantity()) + qty);
                        base.setUpdateBy(SecurityUtils.getUsername());
                        base.setUpdateTime(new Date());
                        baseMapper.updateById(base);
                    }
                }
            }
        }
        return 1;
    }

    @Override
    public Page<MaterialInbound> selectRecordList(Page<MaterialInbound> page, MaterialInbound inbound)
    {
        return inboundMapper.selectInboundList(page, inbound);
    }

    @Override
    public MaterialInbound selectRecordById(Long id)
    {
        MaterialInbound inbound = inboundMapper.selectById(id);
        if (inbound == null)
        {
            throw new ServiceException("入库记录不存在");
        }
        List<MaterialInboundDetail> details = detailMapper.selectDetailListByInboundId(id);
        inbound.setDetails(details);
        return inbound;
    }

    @Override
    public List<MaterialInboundDetail> selectDetailListByInboundId(Long inboundId)
    {
        return detailMapper.selectDetailListByInboundId(inboundId);
    }

    @Override
    public Page<MaterialInboundDetail> selectDataList(Page<MaterialInboundDetail> page, MaterialInboundDetail detail)
    {
        return detailMapper.selectInboundDataList(page, detail);
    }
}
