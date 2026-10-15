package com.material.common.exception;

import lombok.Getter;

/**
 * 自定义异常
 */
public class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    @Getter
    private Integer code;

    /** 错误信息 */
    private final String message;

    public ServiceException(String message)
    {
        this.message = message;
    }

    public ServiceException(String message, Integer code)
    {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage()
    {
        return message;
    }

}
