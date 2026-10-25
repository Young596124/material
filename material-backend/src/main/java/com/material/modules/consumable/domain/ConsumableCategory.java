package com.material.modules.consumable.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 低值易耗品-物品类别对象 consumable_category
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consumable_category")
public class ConsumableCategory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 类别ID */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /** 类型名称 */
    private String categoryName;

    /** 状态（0启用 1禁用） */
    private String status;

    /** 排序 */
    private Integer orderNum;

    /** 删除标志（0存在 2删除） */
    private String delFlag;
}
