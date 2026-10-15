package com.material.modules.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.common.utils.DateUtils;
import com.material.common.utils.SecurityUtils;
import com.material.modules.material.domain.WarehouseMaterialBase;
import com.material.modules.material.mapper.WarehouseMaterialBaseMapper;
import com.material.modules.material.service.IWarehouseMaterialBaseService;
import com.material.modules.system.domain.SysDictData;
import com.material.modules.system.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1F线边仓物料基表 业务实现
 */
@Service
public class WarehouseMaterialBaseServiceImpl implements IWarehouseMaterialBaseService
{
    @Autowired
    private WarehouseMaterialBaseMapper baseMapper;

    @Autowired
    private ISysDictDataService dictDataService;

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

    /** 来源类型字典类型 */
    private static final String SOURCE_TYPE_DICT = "material_source_type";

    @Override
    public Page<WarehouseMaterialBase> selectList(Page<WarehouseMaterialBase> page, WarehouseMaterialBase base)
    {
        return baseMapper.selectPage(page,
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .like(base.getMaterialCode() != null && !"".equals(base.getMaterialCode()),
                                WarehouseMaterialBase::getMaterialCode, base.getMaterialCode())
                        .like(base.getMaterialName() != null && !"".equals(base.getMaterialName()),
                                WarehouseMaterialBase::getMaterialName, base.getMaterialName())
                        .eq(base.getSourceType() != null && !"".equals(base.getSourceType()),
                                WarehouseMaterialBase::getSourceType, base.getSourceType())
                        .like(base.getLocation() != null && !"".equals(base.getLocation()),
                                WarehouseMaterialBase::getLocation, base.getLocation())
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public WarehouseMaterialBase selectById(Long id)
    {
        return baseMapper.selectById(id);
    }

    @Override
    public List<WarehouseMaterialBase> selectAll()
    {
        return baseMapper.selectList(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public List<WarehouseMaterialBase> selectByMaterialName(String materialName)
    {
        return baseMapper.selectList(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .like(materialName != null && !"".equals(materialName),
                                WarehouseMaterialBase::getMaterialName, materialName)
                        .orderByDesc(WarehouseMaterialBase::getId));
    }

    @Override
    public int updateBase(WarehouseMaterialBase base)
    {
        base.setUpdateBy(SecurityUtils.getNickName());
        base.setUpdateTime(new Date());
        return baseMapper.updateById(base);
    }

    @Override
    public WarehouseMaterialBase selectByMaterialCode(String materialCode)
    {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<WarehouseMaterialBase>()
                        .eq(WarehouseMaterialBase::getMaterialCode, materialCode));
    }

    @Override
    public int importBatch(List<WarehouseMaterialBase> list)
    {
        if (list == null || list.isEmpty()) return 0;
        // 加载来源类型字典，构建 标签->值 映射
        Map<String, String> labelToValue = loadSourceTypeLabelToValueMap();
        int count = 0;
        String nickName = SecurityUtils.getNickName();
        Date now = new Date();
        for (WarehouseMaterialBase b : list)
        {

            // 将来源类型标签转换为字典值（若已是字典值则保持不变）
            b.setMaterialCode(generateMaterialCode());
            b.setSourceType(convertSourceTypeLabelToValue(b.getSourceType(), labelToValue));
            b.setCreateBy(nickName);
            b.setCreateTime(now);
            b.setUpdateBy(nickName);
            b.setUpdateTime(now);
            if (b.getTotalInbound() == null) b.setTotalInbound(0);
            if (b.getRemainingQuantity() == null) b.setRemainingQuantity(0);
            if (b.getOutboundQuantity() == null) b.setOutboundQuantity(0);
            baseMapper.insert(b);
            count++;
        }
        return count;
    }

    /**
     * 加载来源类型字典的 标签->值 映射
     */
    private Map<String, String> loadSourceTypeLabelToValueMap()
    {
        Map<String, String> map = new HashMap<>();
        List<SysDictData> dictList = dictDataService.selectDictDataByType(SOURCE_TYPE_DICT);
        if (dictList != null)
        {
            for (SysDictData d : dictList)
            {
                if (d.getDictLabel() != null)
                {
                    map.put(d.getDictLabel(), d.getDictValue());
                }
            }
        }
        return map;
    }

    /**
     * 将来源类型标签转换为字典值；若输入已是字典值或无法匹配，则原样返回
     */
    private String convertSourceTypeLabelToValue(String sourceType, Map<String, String> labelToValue)
    {
        if (sourceType == null || sourceType.trim().isEmpty()) return sourceType;
        String val = labelToValue.get(sourceType.trim());
        return val != null ? val : sourceType.trim();
    }

    /**
     * 将列表中每条记录的来源类型字典值转换为标签（用于导出展示）
     */
    public List<WarehouseMaterialBase>  convertSourceTypeValueToLabel(List<WarehouseMaterialBase> list)
    {
        if (list == null || list.isEmpty()) return list;
        Map<String, String> valueToLabel = new HashMap<>();
        List<SysDictData> dictList = dictDataService.selectDictDataByType(SOURCE_TYPE_DICT);
        if (dictList != null)
        {
            for (SysDictData d : dictList)
            {
                if (d.getDictValue() != null)
                {
                    valueToLabel.put(d.getDictValue(), d.getDictLabel());
                }
            }
        }
        for (WarehouseMaterialBase b : list)
        {
            if (b.getSourceType() != null)
            {
                String label = valueToLabel.get(b.getSourceType());
                if (label != null)
                {
                    b.setSourceType(label);
                }
            }
        }
        return list;
    }
}
