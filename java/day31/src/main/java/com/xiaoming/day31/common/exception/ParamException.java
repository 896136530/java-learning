package com.xiaoming.day31.common.exception;

import com.xiaoming.day31.common.ResultCode;

/**
 * 参数不合法异常 → HTTP 400
 *
 * 场景举例：
 *   · 价格传了负数：`GET /sku/price?id=1&price=-5`
 *   · 数量传了 0 或负数
 *
 * ⭐ 这就是"业务异常三兄弟"里的老三（另外两个见 ResourceNotFoundException / BizRuleException）。
 *    三个类加起来不到 30 行 —— 因为**具体逻辑都在父类 BaseException 里**，
 *    子类存在的唯一意义就是：**让代码能按"类型"区分错误**。
 */
public class ParamException extends BaseException {

    public ParamException(String message) {
        super(ResultCode.PARAM_ERROR.getHttp(), message);   // 400
    }
}