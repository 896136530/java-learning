package com.xiaoming.day31.common;

/**
 * ⭐ 错误码表 —— 全项目的"错误字典"
 *
 * 【为什么要单独搞一个枚举？】
 *   如果到处写魔法数字（比如 `return 404;`、`e.getMessage().equals("库存不足")`），会出现：
 *     ① 同一个错误，A 处写 400、B 处写 409，前端不知道按哪个判断
 *     ② 想改文案，得全项目搜字符串
 *     ③ 前端同学问"你有哪些错误码"，你答不上来
 *   集中成枚举 → **改一处、全项目生效；前端拿到这张表就知道怎么处理**。
 *
 * 【三段式约定（真实项目几乎都这样）】
 *   code = 业务码，前端用它判断"哪种错"（和 HTTP 状态码是两码事，见下）
 *   http = HTTP 状态码，让浏览器/网关/日志系统一眼看出"成功还是失败"
 *   msg  = 给人看的默认文案（可以被子类覆盖，比如带上"你查的是 id=999"）
 *
 * ⭐ code 和 http 为什么要分开？—— Day30 你已经踩过坑了：
 *    `/tasks/999` 返回 HTTP **200** + 空白响应体。
 *    调用方（前端）看到 200 以为是成功，解析 JSON 却什么都没有 → 只能当成"服务器坏了"。
 *    Day31 的规矩：**错误就用 4xx/5xx 状态码**，同时 JSON 里再给一个业务码做细分。
 */
public enum ResultCode {

    /** 200 成功（正常路径也走统一响应，前端就不用写两套解析逻辑了） */
    SUCCESS("200", 200, "操作成功"),

    /** 400 参数不合法：格式错、值为负、缺字段... */
    PARAM_ERROR("400", 400, "参数不合法"),

    /** 404 资源不存在：查一个没有的数据 */
    NOT_FOUND("404", 404, "资源不存在"),

    /** 409 业务冲突：参数合法、但是违反业务规则（库存不够、状态不允许...） */
    BIZ_CONFLICT("409", 409, "业务规则冲突"),

    /** 500 服务器自己出错了：空指针、数组越界...（⭐ 这种要写日志，但**不能**把堆栈返回给前端） */
    SYSTEM_ERROR("500", 500, "服务器内部错误");

    private final String code;
    private final int http;
    private final String msg;

    ResultCode(String code, int http, String msg) {
        this.code = code;
        this.http = http;
        this.msg = msg;
    }

    public String getCode() { return code; }
    public int getHttp()    { return http; }
    public String getMsg()  { return msg; }

    /** 按 HTTP 状态码反查业务码（BaseException 用的就是它） */
    public static ResultCode ofHttp(int http) {
        for (ResultCode rc : values()) {
            if (rc.http == http) {
                return rc;
            }
        }
        return SYSTEM_ERROR;
    }
}