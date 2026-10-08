package com.xiaoming.day31.common.exception;

import com.xiaoming.day31.common.ResultCode;

/**
 * ⭐ 所有"业务异常"的父类
 *
 * 【为什么要自定义异常，而不直接用 RuntimeException？】
 *   如果 Service 里到处 `throw new RuntimeException("库存不足")`：
 *     ① 全局处理器**分不清**这条是"没找到"还是"业务冲突"（都是 RuntimeException）
 *     ② 只能靠解析 message 字符串判断 → 改个错别字就崩
 *     ③ 调用方看不出这个方法的"错误契约"（有哪些可能出错）
 *   自定义异常把"错误类别"变成**类型信息**，程序就能用类型来判断了。
 *
 * 【继承关系】
 *   RuntimeException
 *     └── BaseException          ← 本类：带 业务码 + HTTP 状态码
 *           ├── ApplicationException      通用业务异常（想偷懒就用它）
 *           ├── ResourceNotFoundException 404（专门给"查不到"用）
 *           └── BizRuleException          409（专门给"业务规则冲突"用）
 *
 * 【为什么继承 RuntimeException 而不是 Exception？】
 *   Exception（受检）→ 每个调用它的方法都得 `throws` 或者 try-catch，污染一大片方法签名
 *   RuntimeException → 不用声明，能"穿透"多层调用，直接被最外层的 @RestControllerAdvice 接住
 *   ⭐ 记住：**业务异常用 RuntimeException**，这是 Spring 项目的主流做法。
 */
public class BaseException extends RuntimeException {

    /** 业务码（字符串，"404" / "409" / "5000x"...） */
    private final String code;

    /** 这个错误该配什么 HTTP 状态码（全局处理器读它来决定 response.setStatus） */
    private final int http;

    /**
     * @param http    建议的 HTTP 状态码（404 / 409 / 400 / 500）
     * @param message 给人看的提示（可以带上上下文，如 "SKU 不存在：id=999"）
     */
    public BaseException(int http, String message) {
        // ⭐ 把 message 交给父类（RuntimeException），这样 getMessage() 能用，日志里也能看到
        super(message);
        this.http = http;
        this.code = ResultCode.ofHttp(http).getCode();
    }

    /** 想自定义业务码时用（比如 "40901" 表示"库存不足"这种细分类） */
    public BaseException(String code, int http, String message) {
        super(message);
        this.http = http;
        this.code = code;
    }

    public String getCode() { return code; }
    public int getHttp()    { return http; }
}