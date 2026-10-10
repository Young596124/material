package com.material.modules.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 物料出库子表对象 material_outbound_detail
 * 该表无 update_by/update_time 字段，通过 @TableField(exist = false) 覆盖父类字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material_outbound_detail")
public class MaterialOutboundDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 出库主表ID */
    private Long outboundId;

    /** 物料基表ID */
    private Long materialBaseId;

    /** 物料名称 */
    private String materialName;

    /** 来源类型 */
    private String sourceType;

    /** 货位 */
    private String location;

    /** 状态（1入库 2取消） */
    private String status;

    /** 剩余数量 */
    private Integer remainingQuantity;

    /** 出库数量 */
    private Integer outboundQuantity;

    /** 覆盖父类字段，数据库中不存在 */
    @TableField(exist = false)
    private String updateBy;

    /** 覆盖父类字段，数据库中不存在 */
    @TableField(exist = false)
    private Date updateTime;
}
