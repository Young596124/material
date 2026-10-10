package com.material.modules.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 物料入库主表对象 material_inbound
 * 该表无 update_by/update_time 字段，通过 @TableField(exist = false) 覆盖父类字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material_inbound")
public class MaterialInbound extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 入库编号（RK+yyyyMMdd+2位流水） */
    private String inboundCode;

    /** 用户ID */
    private Long userId;

    /** 用户名称+工号 */
    private String userName;

    /** 入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inboundDate;

    /** 描述 */
    private String description;

    /** 查询用：开始日期 */
    @TableField(exist = false)
    private String beginDate;

    /** 查询用：结束日期 */
    @TableField(exist = false)
    private String endDate;

    /** 覆盖父类字段，数据库中不存在 */
    @TableField(exist = false)
    private String updateBy;

    /** 覆盖父类字段，数据库中不存在 */
    @TableField(exist = false)
    private Date updateTime;

    /** 入库明细列表（非数据库字段） */
    @TableField(exist = false)
    private List<MaterialInboundDetail> details;
}
