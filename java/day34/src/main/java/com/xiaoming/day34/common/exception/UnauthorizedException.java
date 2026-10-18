package com.xiaoming.day34.common.exception;

import com.xiaoming.day34.common.ResultCode;

/**
 * "没登录 / token 无效" 异常 → HTTP 401
 *
 * ⭐ 这是 Day33 **新增**的第 4 个业务异常（前三兄弟：ParamException 400 /
 *    ResourceNotFoundException 404 / BizRuleException 409）。
 *
 * 【401 和 403 有什么区别？（面试常问）】
 *   ┌────────┬────────────────────────┬──────────────────────────┐
 *   │ 状态码 │ 意思                    │ 例子                      │
 *   ├────────┼────────────────────────┼──────────────────────────┤
 *   │ 401    │ **你是谁？**（没身份）    │ 没带 token / token 过期    │
 *   │ 403    │ **我知道你是谁，但不行**  │ 普通用户想删别人的帖子      │
 *   └────────┴────────────────────────┴──────────────────────────┘
 *   记忆：**401 = 先登录；403 = 登录了也没用**。
 *   📌 本项目今天只做 401（登录校验）；403（权限校验）要等到 Day45 的 RBAC。
 *
 * 【它怎么和枚举、异常处理器串起来】
 *   本类只写一行 `super(401, message)` → 父类 BaseException 用
 *   `ResultCode.ofHttp(401)` 反查出业务码 "401" →
 *   GlobalExceptionHandler① 接住 → `ResponseEntity.status(401)` + 统一 JSON。
 *   ⭐ 三个类各管一件事，谁都不用改谁 —— 这就是 Day31/32 那套设计的红利。
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED.getHttp(), message);   // 401
    }
}