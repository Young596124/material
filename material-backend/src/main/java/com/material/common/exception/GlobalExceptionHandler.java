package com.material.common.exception;

import com.material.common.core.domain.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return code != null ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /** 请求方式不支持 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e)
    {
        log.error(e.getMessage(), e);
        return AjaxResult.error("不支持'" + e.getMethod() + "'请求");
    }

    /** 拦截未知的运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e)
    {
        log.error("运行时异常:", e);
        return AjaxResult.error("系统异常，请联系管理员");
    }

    /** 系统异常 */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e)
    {
        log.error("系统异常:", e);
        return AjaxResult.error("系统异常，请联系管理员");
    }

    /** 参数绑定异常 */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        log.error("参数绑定异常:", e);
        return AjaxResult.error("参数校验失败：" + e.getMessage());
    }
}
