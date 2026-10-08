package com.xiaoming.day31.common.exception;

/**
 * 通用业务异常 —— "我就想抛个错，不想为它专门建类" 时用它
 *
 * 用法：
 *   throw new ApplicationException(ResultCode.BIZ_CONFLICT, "这个名字已经被占用了");
 */
public class ApplicationException extends BaseException {

    public ApplicationException(com.xiaoming.day31.common.ResultCode rc, String message) {
        super(rc.getCode(), rc.getHttp(), message);
    }

    public ApplicationException(String message) {
        super(com.xiaoming.day31.common.ResultCode.BIZ_CONFLICT.getHttp(), message);
    }
}