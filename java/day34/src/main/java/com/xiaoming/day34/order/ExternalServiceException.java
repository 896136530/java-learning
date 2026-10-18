package com.xiaoming.day34.order;

/**
 * ⭐⭐ 「外部服务调用失败」—— 一个**受检异常（Checked Exception）**，今天的关键道具
 *
 * 【它和项目里其他异常最大的区别】
 *   看继承链：
 *     BaseException extends **RuntimeException**   ← 我们自己的业务异常（400/404/409）
 *     ExternalServiceException extends **Exception** ← 本类：受检异常
 *
 *   这个差别在今天不是"风格问题"，而是**决定事务回不回滚**的问题：
 *     Spring 的默认回滚规则是 —— **只对 RuntimeException 和 Error 回滚**，
 *     受检异常**默认不回滚**（会直接 commit！）。
 *
 * 【为什么偏偏用一个受检异常来演这个戏？—— 因为"真实"】
 *   受检异常在真实项目里最常见的来源就是**调用外部服务**：
 *   银行接口、短信、积分、支付回调、第三方 HTTP SDK……
 *   这些库的作者很自然地写成 `throws IOException` / 自定义受检异常 ——
 *   于是一个"下单成功但积分没加上"的受检异常，会**悄悄地把半个订单提交进数据库**。
 *   这就是面试题"事务失效场景"里最阴的一条。
 *
 * 【为什么它不 extends BaseException？】
 *   因为 BaseException 那一套的前提是"继承 RuntimeException"（见 BaseException 的注释）。
 *   受检异常**必须**在方法签名上 `throws`，不然编译不过 —— 这是 Java 的要求。
 *   ⭐ 所以你会在 OrderService 的方法签名上看到 `throws ExternalServiceException`，
 *      这不是啰嗦，而是"这个错误类型确实要从这里穿出去"的声明。
 *
 * 📌 还有一个副作用值得记住：因为它**不是** BaseException，
 *    所以全局异常处理器接不住它（不会被 ① 号 handler 接），
 *    最终会掉到 ⑤ 号**兜底 handler** → HTTP **500** + "服务器开小差了，请稍后重试"。
 */
public class ExternalServiceException extends Exception {

    public ExternalServiceException(String message) {
        super(message);
    }
}