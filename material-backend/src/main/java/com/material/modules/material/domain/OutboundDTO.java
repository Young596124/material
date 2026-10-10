package com.material.modules.material.domain;

import lombok.Data;

import java.util.List;

/**
 * 出库表单提交DTO
 */
@Data
public class OutboundDTO
{
    /** 用户ID */
    private Long userId;

    /** 用户名称+工号 */
    private String userName;

    /** 描述 */
    private String description;

    /** 出库子表数据 */
    private List<MaterialOutboundDetail> details;
}
