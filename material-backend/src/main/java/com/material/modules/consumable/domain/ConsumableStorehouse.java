package com.material.modules.consumable.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 低值易耗品-库房对象 consumable_storehouse
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("consumable_storehouse")
public class ConsumableStorehouse extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 库房ID */
    @TableId(value = "storehouse_id", type = IdType.AUTO)
    private Long storehouseId;

    /** 库房编号（KF+三位流水号） */
    private String storehouseCode;

    /** 仓库名称（唯一） */
    private String storehouseName;

    /** 责任部门ID */
    private Long deptId;

    /** 责任部门名称（冗余） */
    private String deptName;

    /** 仓库地址 */
    private String address;

    /** 状态（0启用 1停用） */
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;
}
