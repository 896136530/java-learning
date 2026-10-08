package com.xiaoming.day31.common.exception;

import com.xiaoming.day31.common.ResultCode;

/**
 * "查不到" 异常 → HTTP 404
 *
 * ⭐ 这就是 Day30 TODO③ 那个坑的正规解法！
 *   Day30：`return taskRepository.findById(id).orElse(null);`
 *           → /tasks/999 返回 **HTTP 200 + 空白响应体**，调用方分不清"没找到"和"服务器坏了"
 *   Day31：`throw new ResourceNotFoundException("SKU 不存在：id=" + id);`
 *           → 全局处理器接住 → **HTTP 404 + {"code":"404","message":"SKU 不存在：id=999"}**
 *
 * 【为什么用"异常"而不是"返回 null"？】
 *   返回 null 的问题：**调用方可以忽略它**（不判空就继续往下走 → 后面空指针炸在别处，很难查）
 *   抛异常的好处：**跑不下去了就必须处理**，而且能被统一收口，不用在每个 Controller 里写 if。
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(ResultCode.NOT_FOUND.getHttp(), message);   // 404
    }

    /** 偷懒写法：自动拼出 "SKU 不存在：id=999" */
    public static ResourceNotFoundException of(String what, Object id) {
        return new ResourceNotFoundException(what + " 不存在：id=" + id);
    }
}