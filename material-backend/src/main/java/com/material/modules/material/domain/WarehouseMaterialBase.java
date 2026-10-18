package com.material.modules.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 1F线边仓物料基表对象 warehouse_material_base
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("warehouse_material_base")
public class WarehouseMaterialBase extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 物料编码 */
    private String materialCode;

    /** 物料名称 */
    private String materialName;

    /** 需求人ID */
    private Long applicantId;

    /** 需求人 */
    private String applicantName;

    /** 物料所属项目系统 */
    private String projectSystem;

    /** 来源类型 */
    private String sourceType;

    /** 货位 */
    private String location;

    /** 附件 */
    private String attachment;

    /** 特殊说明 */
    private String specialNote;

    /** 入库总数 */
    private Integer totalInbound;

    /** 剩余数量 */
    private Integer remainingQuantity;

    /** 出库数量 */
    private Integer outboundQuantity;

    /** 仓库ID */
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;
}
