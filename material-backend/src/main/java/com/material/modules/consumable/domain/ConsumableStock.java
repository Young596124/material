package com.material.modules.consumable.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 低值易耗品-库存信息对象 consumable_stock
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consumable_stock")
public class ConsumableStock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 库房ID */
    private Long storehouseId;

    /** 物品ID */
    private Long goodsId;

    /** 货物类型 */
    private String categoryName;

    /** 货物等级 */
    private String goodsLevel;

    /** 货物名称 */
    private String goodsName;

    /** 货物编号 */
    private String goodsCode;

    /** 货物图片 */
    private String goodsImage;

    /** 库存数量 */
    private Integer stockQuantity;

    /** 入库数量（累计） */
    private Integer inboundQuantity;

    /** 领用数量（累计） */
    private Integer usedQuantity;

    /** 退库数量（累计） */
    private Integer returnQuantity;

    /** 单位 */
    private String unit;

    /** 货位号 */
    private String location;

    /** 库房名称 */
    private String storehouseName;

    /** 库房编号 */
    private String storehouseCode;

    /** 库房地址 */
    private String storehouseAddress;

    /** 管理员（责任部门） */
    private String manager;
}
