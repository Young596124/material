package com.material.modules.material.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.material.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物料编码申请对象 material_code_application
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("material_code_application")
public class MaterialCodeApplication extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 物料编码 */
    private String materialCode;

    /** 需求人ID */
    private Long applicantId;

    /** 需求人名称 */
    private String applicantName;

    /** 物料名称 */
    private String materialName;

    /** 物料所属项目系统 */
    private String projectSystem;

    /** 来源类型（字典 material_source_type） */
    private String sourceType;

    /** 货位 */
    private String location;

    /** 附件 */
    private String attachment;

    /** 特殊说明 */
    private String specialNote;
}
