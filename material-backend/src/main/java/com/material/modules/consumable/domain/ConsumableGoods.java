package com.material.modules.consumable.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 低值易耗品-物品信息对象 consumable_goods
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consumable_goods")
public class ConsumableGoods extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 物品ID */
    @TableId(value = "goods_id", type = IdType.AUTO)
    private Long goodsId;

    /** 货物类型（类别ID） */
    private Long categoryId;

    /** 类型名称（冗余） */
    private String categoryName;

    /** 货物编号（大类名称首字母大写+六位流水号） */
    private String goodsCode;

    /** 货物名称（不可重复） */
    private String goodsName;

    /** 货物单价 */
    private BigDecimal price;

    /** 货物单位（字典 consumable_unit） */
    private String unit;

    /** 消耗类型（0无 1例行消耗-月度 2例行消耗-季度 3例行消耗-半年度） */
    private String consumeType;

    /** 显示状态（0显示 1隐藏） */
    private String showStatus;

    /** 等级（A B C） */
    @TableField("`level`")
    private String level;

    /** 描述 */
    private String description;

    /** 图片 */
    private String image;

    /** 状态（0启用 1禁用） */
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;
}
