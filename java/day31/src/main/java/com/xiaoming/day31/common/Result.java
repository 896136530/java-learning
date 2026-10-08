package com.xiaoming.day31.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ⭐ 统一响应体 —— 所有接口**成功和失败都返回这个形状**
 *
 * 成功的 JSON：
 *   { "code":"200", "message":"操作成功", "data":{...} }
 * 失败的 JSON：
 *   { "code":"404", "message":"SKU 不存在：id=999", "data":null }
 *
 * 【为什么要"统一"？】
 *   前端最痛的场景是：每个接口返回形状都不一样，于是写一堆 if-else：
 *     if (res.status === 200) { ... } else if (res.data.error) { ... }
 *   统一之后前端只写一次：
 *     const r = await fetch(...)
 *     if (!r.ok || r.body.code !== '200') { 全局弹错误提示 }
 *     else { 用 r.body.data }
 *
 * 【data 为 null 时 JSON 里长什么样？】
 *   Jackson 默认**不忽略 null**，会输出 "data":null（前端拿到 null 好判断）
 *   想彻底不输出这个字段，在字段上加 @JsonInclude(JsonInclude.Include.NON_NULL)
 */
public class Result<T> {

    /** 业务码（"200" 表示成功；其它见 ResultCode） */
    private String code;

    /** 给人看的提示语（可以直接弹给用户） */
    private String message;

    /** 真正的业务数据；出错时是 null */
    private T data;

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ============ 成功：两种写法 ============

    /** 成功 + 带数据（最常用） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /** 成功 + 不带数据（比如删除接口） */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    // ============ 失败：三种写法 ============

    /** 用错误码表里的默认文案 */
    public static <T> Result<T> fail(ResultCode rc) {
        return new Result<>(rc.getCode(), rc.getMsg(), null);
    }

    /** 用自定义文案（更常写，因为能带上上下文，比如 "SKU 不存在：id=999"） */
    public static <T> Result<T> fail(ResultCode rc, String message) {
        return new Result<>(rc.getCode(), message, null);
    }

    /**
     * ⭐ 额外字段：哪些参数错了
     * 校验失败时特别好用，形如：
     *   { "title":"书名不能为空", "price":"价格不能小于 0" }
     *
     * 为什么要另开一个字段而不是塞进 message？
     *   因为**前端要按字段名把红字标在对应输入框旁边**，
     *   塞成一句话 "title不能为空,price不能小于0" 前端就得自己切字符串。
     */
    private Map<String, String> fieldErrors;

    public static <T> Result<T> failFields(ResultCode rc, String message, Map<String, String> fieldErrors) {
        Result<T> r = new Result<>(rc.getCode(), message, null);
        r.fieldErrors = fieldErrors;
        return r;
    }

    /** 小工具：按"字段名 → 错误提示"顺序收集（LinkedHashMap 保证顺序和前端展示一致） */
    public static Map<String, String> fieldErrors() {
        return new LinkedHashMap<>();
    }

    // ============ Jackson 靠这些 getter 把对象转成 JSON ============
    // ⚠️ Day30 踩过的坑：忘了写 getter → JSON 里字段是 null
    public String getCode()    { return code; }
    public String getMessage() { return message; }
    public T getData()         { return data; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}