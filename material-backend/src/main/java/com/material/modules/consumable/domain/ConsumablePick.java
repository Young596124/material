package com.material.modules.consumable.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 低值易耗品-物品领用对象 consumable_pick
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consumable_pick")
public class ConsumablePick extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 领用编号（LY+yyyyMMdd+3位流水） */
    private String pickCode;

    /** 货物类型ID */
    private Long categoryId;

    /** 货物类型名称 */
    private String categoryName;

    /** 物品ID */
    private Long goodsId;

    /** 货物名称 */
    private String goodsName;

    /** 货物编号 */
    private String goodsCode;

    /** 领用总数 */
    private Integer quantity;

    /** 单位 */
    private String unit;

    /** 领用库房ID */
    private Long storehouseId;

    /** 库房名称 */
    private String storehouseName;

    /** 库房编号 */
    private String storehouseCode;

    /** 库房地址 */
    private String storehouseAddress;

    /** 责任部门 */
    private String deptName;

    /** 备注 */
    private String remark;

    /** 领用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pickTime;

    /** 删除标志（0存在 2删除） */
    private String delFlag;
}
