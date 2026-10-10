package com.material.modules.material.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 入库表单提交DTO
 */
@Data
public class InboundDTO
{
    /** 用户ID */
    private Long userId;

    /** 用户名称+工号 */
    private String userName;

    /** 入库日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date inboundDate;

    /** 描述 */
    private String description;

    /** 入库子表数据 */
    private List<MaterialInboundDetail> details;
}
