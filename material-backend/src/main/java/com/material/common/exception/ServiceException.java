package com.material.common.exception;

/**
 * 自定义异常
 */
public class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private Integer code;

    /** 错误信息 */
    private String message;

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

    public Integer getCode()
    {
        return code;
    }
}
