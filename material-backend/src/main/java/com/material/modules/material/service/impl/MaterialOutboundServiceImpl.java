package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.exception.ServiceException;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.*;
import com.material.modules.material.mapper.MaterialOutboundDetailMapper;
import com.material.modules.material.mapper.MaterialOutboundMapper;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IMaterialOutboundService;
import com.material.modules.system.domain.SysDictData;
import com.material.modules.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ISysDictDataService dictDataService;

    /** 来源类型字典类型 */
    private static final String SOURCE_TYPE_DICT = "material_source_type";

    /** 出库状态字典类型 */
    private static final String OUTBOUND_STATUS_DICT = "material_outbound_status";

    /**
     * 生成出库编号：CK + yyyyMMdd + 2位流水号
     */
    private String generateOutboundCode()
    {
        String today = DateUtils.dateTimeNow("yyyyMMdd");
        Long seq = redisTemplate.opsForValue().increment("material:outbound:seq:" + today, 1);
        return "CK" + today + String.format("%03d", seq);
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
        outbound.setCreateBy(SecurityUtils.getNickName());
        outbound.setCreateTime(new Date());
        outboundMapper.insert(outbound);

        // 保存子表并更新库存
        if (dto.getDetails() != null)
        {
            for (MaterialOutboundDetail detail : dto.getDetails())
            {
                detail.setOutboundId(outbound.getId());
                detail.setCreateBy(SecurityUtils.getNickName());
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

    @Override
    public List<MaterialOutbound> selectExportList(MaterialOutbound outbound)
    {
        List<MaterialOutbound> mainList = outboundMapper.selectOutboundExportList(outbound);
        if (mainList == null || mainList.isEmpty())
        {
            return new ArrayList<>();
        }
        // 批量查询所有主表对应的明细
        List<Long> ids = mainList.stream().map(MaterialOutbound::getId).collect(Collectors.toList());
        List<MaterialOutboundDetail> details = detailMapper.selectDetailListByOutboundIds(ids);
        // 来源类型、状态 字典值转标签
        Map<String, String> sourceMap = loadDictValueLabelMap(SOURCE_TYPE_DICT);
        Map<String, String> statusMap = loadDictValueLabelMap(OUTBOUND_STATUS_DICT);
        Map<Long, List<MaterialOutboundDetail>> grouped = new HashMap<>();
        if (details != null)
        {
            for (MaterialOutboundDetail d : details)
            {
                d.setSourceType(convertDict(sourceMap, d.getSourceType()));
                d.setStatus(convertDict(statusMap, d.getStatus()));
                grouped.computeIfAbsent(d.getOutboundId(), k -> new ArrayList<>()).add(d);
            }
        }
        for (MaterialOutbound m : mainList)
        {
            m.setDetails(grouped.getOrDefault(m.getId(), new ArrayList<>()));
        }
        return mainList;
    }

    @Override
    public List<MaterialOutboundDetail> selectOutboundDataExportList() {
        List<MaterialOutboundDetail> list = detailMapper.selectList(new LambdaQueryWrapper<MaterialOutboundDetail>()
                .orderByDesc(MaterialOutboundDetail::getCreateTime)
        );
        // 来源类型、状态 字典值转标签
        Map<String, String> sourceMap = loadDictValueLabelMap(SOURCE_TYPE_DICT);
        Map<String, String> statusMap = loadDictValueLabelMap(OUTBOUND_STATUS_DICT);
        if (list != null){
            for (MaterialOutboundDetail d : list){
                d.setSourceType(convertDict(sourceMap, d.getSourceType()));
                d.setStatus(convertDict(statusMap, d.getStatus()));
            }
        }
        return list;
    }

    /**
     * 加载字典 值->标签 映射
     */
    private Map<String, String> loadDictValueLabelMap(String dictType)
    {
        Map<String, String> map = new HashMap<>();
        List<SysDictData> dictList = dictDataService.selectDictDataByType(dictType);
        if (dictList != null)
        {
            for (SysDictData d : dictList)
            {
                if (d.getDictValue() != null)
                {
                    map.put(d.getDictValue(), d.getDictLabel());
                }
            }
        }
        return map;
    }

    /**
     * 字典值转标签，无匹配时原样返回
     */
    private String convertDict(Map<String, String> map, String value)
    {
        if (value == null || value.trim().isEmpty()) return value;
        String label = map.get(value);
        return label != null ? label : value;
    }
}
