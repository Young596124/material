package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.material.modules.system.domain.SysDictData;
import com.material.modules.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        return "RK" + today + String.format("%03d", seq);
    }

    /** 来源类型字典类型 */
    private static final String SOURCE_TYPE_DICT = "material_source_type";

    /** 入库状态字典类型 */
    private static final String INBOUND_STATUS_DICT = "material_inbound_status";

    @Autowired
    private ISysDictDataService dictDataService;

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
        inbound.setCreateBy(SecurityUtils.getNickName());
        inbound.setCreateTime(new Date());
        inbound.setUpdateBy(SecurityUtils.getNickName());
        inbound.setUpdateTime(new Date());
        inboundMapper.insert(inbound);

        // 保存子表并更新库存
        if (dto.getDetails() != null)
        {
            for (MaterialInboundDetail detail : dto.getDetails())
            {
                detail.setInboundId(inbound.getId());
                detail.setCreateBy(SecurityUtils.getNickName());
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


    @Override
    public List<MaterialInbound> selectExportList(MaterialInbound inbound)
    {
        List<MaterialInbound> mainList = inboundMapper.selectInboundExportList(inbound);
        if (mainList == null || mainList.isEmpty())
        {
            return new ArrayList<>();
        }
        // 批量查询所有主表对应的明细
        List<Long> ids = mainList.stream().map(MaterialInbound::getId).collect(Collectors.toList());
        List<MaterialInboundDetail> details = detailMapper.selectDetailListByInboundIds(ids);
        // 来源类型、状态 字典值转标签
        Map<String, String> sourceMap = loadDictValueLabelMap(SOURCE_TYPE_DICT);
        Map<String, String> statusMap = loadDictValueLabelMap(INBOUND_STATUS_DICT);
        Map<Long, List<MaterialInboundDetail>> grouped = new HashMap<>();
        if (details != null){
            for (MaterialInboundDetail d : details){
                d.setSourceType(convertDict(sourceMap, d.getSourceType()));
                d.setStatus(convertDict(statusMap, d.getStatus()));
                grouped.computeIfAbsent(d.getInboundId(), k -> new ArrayList<>()).add(d);
            }
        }
        for (MaterialInbound m : mainList){
            m.setDetails(grouped.getOrDefault(m.getId(), new ArrayList<>()));
        }
        return mainList;
    }

    @Override
    public List<MaterialInboundDetail> selectInboundDataExportList() {
        List<MaterialInboundDetail> list = detailMapper.selectList(new LambdaQueryWrapper<MaterialInboundDetail>()
                .orderByDesc(MaterialInboundDetail::getCreateTime)
        );
        // 来源类型、状态 字典值转标签
        Map<String, String> sourceMap = loadDictValueLabelMap(SOURCE_TYPE_DICT);
        Map<String, String> statusMap = loadDictValueLabelMap(INBOUND_STATUS_DICT);
        if (list != null){
            for (MaterialInboundDetail d : list){
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
