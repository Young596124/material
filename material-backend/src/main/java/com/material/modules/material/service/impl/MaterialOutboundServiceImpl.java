package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.MaterialOutbound;
import com.material.modules.material.domain.MaterialOutboundDetail;
import com.material.modules.material.domain.OutboundDTO;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.MaterialOutboundDetailMapper;
import com.material.modules.material.mapper.MaterialOutboundMapper;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IMaterialOutboundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 物料出库 业务实现
 */
@Service
public class MaterialOutboundServiceImpl implements IMaterialOutboundService
{
    @Autowired
    private MaterialOutboundMapper outboundMapper;

    @Autowired
    private MaterialOutboundDetailMapper detailMapper;

    @Autowired
    private WarehouseMaterialBaseMapper baseMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成出库编号：CK + yyyyMMdd + 2位流水号
     */
    private String generateOutboundCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("material:outbound:seq:" + today, 1);
        return "CK" + today + String.format("%02d", seq);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOutbound(OutboundDTO dto)
    {
        // 保存出库主表
        MaterialOutbound outbound = new MaterialOutbound();
        outbound.setOutboundCode(generateOutboundCode());
        outbound.setUserId(dto.getUserId());
        outbound.setUserName(dto.getUserName());
        outbound.setDescription(dto.getDescription());
        outbound.setCreateBy(SecurityUtils.getUsername());
        outbound.setCreateTime(new Date());
        outboundMapper.insert(outbound);

        // 保存子表并更新库存
        if (dto.getDetails() != null)
        {
            for (MaterialOutboundDetail detail : dto.getDetails())
            {
                detail.setOutboundId(outbound.getId());
                detail.setCreateBy(SecurityUtils.getUsername());
                detail.setCreateTime(new Date());

                // 校验出库数量不大于剩余数量
                if ("1".equals(detail.getStatus()) && detail.getMaterialBaseId() != null)
                {
                    WarehouseMaterialBase base = baseMapper.selectById(detail.getMaterialBaseId());
                    if (base != null)
                    {
                        int remaining = base.getRemainingQuantity() == null ? 0 : base.getRemainingQuantity();
                        int outQty = detail.getOutboundQuantity() == null ? 0 : detail.getOutboundQuantity();
                        if (outQty > remaining)
                        {
                            throw new ServiceException("物料[" + detail.getMaterialName() + "]出库数量(" + outQty
                                    + ")不能大于剩余数量(" + remaining + ")");
                        }
                        base.setRemainingQuantity(remaining - outQty);
                        base.setOutboundQuantity((base.getOutboundQuantity() == null ? 0 : base.getOutboundQuantity()) + outQty);
                        base.setUpdateBy(SecurityUtils.getUsername());
                        base.setUpdateTime(new Date());
                        baseMapper.updateById(base);
                        // 刷新明细中的剩余数量为扣减后的值
                        detail.setRemainingQuantity(base.getRemainingQuantity());
                    }
                }
                detailMapper.insert(detail);
            }
        }
        return 1;
    }

    @Override
    public Page<MaterialOutbound> selectRecordList(Page<MaterialOutbound> page, MaterialOutbound outbound)
    {
        return outboundMapper.selectOutboundList(page, outbound);
    }

    @Override
    public MaterialOutbound selectRecordById(Long id)
    {
        MaterialOutbound outbound = outboundMapper.selectById(id);
        if (outbound == null)
        {
            throw new ServiceException("出库记录不存在");
        }
        List<MaterialOutboundDetail> details = detailMapper.selectDetailListByOutboundId(id);
        outbound.setDetails(details);
        return outbound;
    }

    @Override
    public List<MaterialOutboundDetail> selectDetailListByOutboundId(Long outboundId)
    {
        return detailMapper.selectDetailListByOutboundId(outboundId);
    }

    @Override
    public Page<MaterialOutboundDetail> selectDataList(Page<MaterialOutboundDetail> page, MaterialOutboundDetail detail)
    {
        return detailMapper.selectOutboundDataList(page, detail);
    }
}
