package com.material.modules.material.domain;

import lombok.Data;

/**
 * 物料编码申请对象 material_code_application
 */
@Data
public class MaterialCodeApplicationDTO {

    /** 物料编码 */
    private String materialCode;

    /** 需求人ID */
    private Long applicantId;

    /** 需求人名称 */
    private String applicantName;

    /** 仓库ID */
    private Long warehouseId;

    /** 仓库名称 */
    private String warehouseName;

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
