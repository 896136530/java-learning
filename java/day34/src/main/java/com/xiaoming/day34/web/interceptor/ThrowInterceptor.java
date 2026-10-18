package com.xiaoming.day34.web.interceptor;

import com.xiaoming.day34.common.ResultCode;
import com.xiaoming.day34.common.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ⭐ 实验用拦截器：**在 preHandle 里故意抛异常**
 *
 * 【为什么要有这么个"没用"的类？】
 *   因为它要回答一个真实项目里**很容易踩、又很难查**的问题：
 *
 *   > 拦截器里抛的异常，会不会被 `@RestControllerAdvice` 接住？
 *
 *   两种后果差别巨大：
 *     · 会接住 → 返回我们统一的 JSON（409/500 都一样好看）
 *     · 不接住 → 返回 SpringBoot 自带的错误格式（字段名是 timestamp/status/error/path，前端解析不了）
 *   这种事**别猜、别背博客**，写个实验打一发就知道了 —— 这就是今天"动手 > 记忆"的态度。
 *
 *   🎯 真机实测结论（见 Day33 的 `练习题和代码题目.md` 第 1 步观察点④）：
 *      **会被接住** → 返回 409 + 我们的统一 JSON。
 *      原因：preHandle 是在 `DispatcherServlet.doDispatch` 的 try 块里被调用的，
 *      抛出的异常会走进 `processDispatchResult` → 交给异常处理器链 → 找到我们的 advice。
 *      📌 所以"拦截器抛异常一定不被处理"这句流传很广的说法，**在 Spring 6 上是错的**。
 *
 * 【⭐ 本类为什么可以直接 new？】
 *   因为它**没有任何依赖**（没有字段要注入）。
 *   `new ThrowInterceptor()` 完全够用 —— 和 AuthInterceptor 形成鲜明对照：
 *   **需要注入依赖的拦截器必须交给 Spring；纯逻辑的拦截器 new 出来没问题。**
 *
 * 【⚠️ 这个类挂在 /trace/throw/** 上，只影响那一个调试接口，不影响业务接口。】
 */
public class ThrowInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 用 ApplicationException（继承 BaseException）→ 本该是 409 + 统一 JSON
        throw new ApplicationException(ResultCode.BIZ_CONFLICT, "拦截器里故意抛的异常（preHandle）");
    }
}