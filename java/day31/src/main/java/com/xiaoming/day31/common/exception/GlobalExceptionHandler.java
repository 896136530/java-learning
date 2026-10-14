package com.xiaoming.day31.common.exception;

import com.xiaoming.day31.common.Result;
import com.xiaoming.day31.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
// ⭐ 新东西：访问了一个"没有对应接口"的网址时，Spring 6 抛的就是它（相当于 404）
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * ⭐⭐⭐ 全局异常处理器 —— 今天的主角
 *
 * 【一句话说清它干嘛】
 *   整个项目里**任何人、任何地方** throw 出来的异常，最后都会**飞到这里**，
 *   由这里决定"返回什么状态码 + 什么 JSON"。
 *   → Controller / Service 里再也不用写 try-catch 了。
 *
 * 【@RestControllerAdvice 是什么？】
 *   @ControllerAdvice  = "给所有 Controller 加一层公共逻辑"（AOP 思想，Day42 会讲原理）
 *   @RestControllerAdvice = @ControllerAdvice + @ResponseBody（返回值自动转 JSON）
 *
 * 【它和 try-catch 的对比】
 *   没有它（Day30 的写法）：
 *     @GetMapping("/{id}")
 *     public Task one(@PathVariable Long id) {
 *         Task t = repo.findById(id).orElse(null);
 *         if (t == null) { ...要自己拼错误 JSON、自己设状态码... }   ← 每个接口都要写一遍
 *         return t;
 *     }
 *   有了它（今天的写法）：
 *     @GetMapping("/{id}")
 *     public Result<Sku> one(@PathVariable Long id) {
 *         return Result.ok(skuService.getById(id));   // ← Service 里 throw，Controller 什么都不管
 *     }
 *   ⭐ 这就叫"**把横切关注点抽出去**" —— 错误处理只写一次，全项目生效。
 *
 * 【匹配规则（重要）】
 *   同一个异常有多个 @ExceptionHandler 都能处理时，Spring 选**最具体**的那个：
 *     ParamException（子类） 优先于  BaseException（父类） 优先于  Exception（兜底）
 *   所以顺序是：先写具体的，最后写 Exception 兜底。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ① 兜住所有自定义业务异常（BaseException 及其所有子类）
     *    这是最常用的一条：404 / 400 / 409 全从这走。
     *
     * ⭐ 用 ResponseEntity 而不是直接 return Result：
     *    因为我们需要**同时控制**「HTTP 状态码」和「响应体」。
     *    直接 return Result 只能控制响应体，状态码会是 200 —— 那又退化成 Day30 的坑了。
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBase(BaseException e) {
        // 业务异常是"预期内"的，用 warn 级别记一下即可（不要打堆栈，太吵）
        log.warn("业务异常：code={}, http={}, message={}", e.getCode(), e.getHttp(), e.getMessage());
        return ResponseEntity.status(e.getHttp())
                .body(Result.fail(ResultCode.ofHttp(e.getHttp()), e.getMessage()));
    }

    /**
     * ② 参数校验失败（@Valid 校验 DTO 时抛的）
     *
     * 什么时候触发？比如：
     *   POST /sku  请求体 {"title":"", "price":-1}
     *   而 SkuForm 上有 @NotBlank / @Min 注解 → Spring 抛 MethodArgumentNotValidException
     *
     * 这里把每个字段的错误**分开收集**成 map：{"title":"书名不能为空", "price":"价格不能小于 0"}
     * 这样前端能直接把红字标在对应输入框旁边。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = Result.fieldErrors();
        e.getBindingResult().getFieldErrors().forEach(fe ->
                // getDefaultMessage() 就是注解里写的 message，如 @NotBlank(message="书名不能为空")
                errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        log.warn("参数校验失败：{}", errors);
        return ResponseEntity.status(400)
                .body(Result.failFields(ResultCode.PARAM_ERROR, "参数校验失败，请看 fieldErrors", errors));
    }

    /**
     * ③ TODO①（✅ 已完成）：兜底处理 —— 拦住所有"没预料到"的异常
     *
     * 为什么必须有它？——
     *   · 空指针、数组越界、除零……这类异常如果没人处理，
     *     Spring 会返回它自带的 Whitelabel 错误页（HTML！），前端根本没法解析。
     *   · 而且**堆栈信息会暴露给外面**（表名、类名、甚至密码），这是安全问题。
     *
     * 【本条已实现，拆成下面 ④ 和 ⑤ 两个 handler】实现时的三条要求：
     *     ① 注解：@ExceptionHandler(Exception.class)
     *     ② **把完整堆栈写进日志**：log.error("系统异常", e);
     *        ⭐ 和上面 ①② 的区别：前两个是"预期内的"，只记一行；这个是"出 bug 了"，
     *           **必须打完整堆栈**，否则线上排查无从下手。
     *     ③ 返回 500 + 统一格式，但**消息只能给一句笼统的**：
     *        Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试")
     *        ⚠️ 绝对不能把 e.getMessage() 返回给前端（会泄露内部实现）
     *
     * 🎯 实测结论（2026-10-14 真机验证通过）：
     *   GET /sku/boom → HTTP 500 + {"code":"500","message":"服务器开小差了，请稍后重试"}
     *   日志里有 ERROR + 完整堆栈，响应体里**看不到** NullPointerException 字样 ✅
     *
     * ⚠️ 实现过程中的一个副作用（第一版只有 ⑤，被真机测试抓出来了）：
     *   兜底把 `NoResourceFoundException`（网址不存在）也接住了 → `/abc` 被报成 **500**。
     *   → 教训：**兜底越宽，越容易误伤"本该精确处理"的情况**。
     *     有些异常看着像"意外"，其实很常见（网址打错天天有），必须单独配 handler。
     *     → 这就是 ④ 存在的理由。
     */

    /**
     * ④ 网址写错了（访问了一个没有对应接口的路径）
     *
     * ⭐ 为什么单独写一条？—— 因为 Spring 6 里"网址不存在"抛的是
     *    `NoResourceFoundException`，它**也是 Exception 的子类**，
     *    所以如果不单独接，就会被下面的 ⑤ 兜底接成 **500**（"服务器炸了"）。
     *    那种后果：用户手抖输错网址 → 监控全站报警"服务器错误"，运维要被吵死。
     *    所以这条**必须比兜底更具体**，放在兜底**之前**才对。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("网址不存在：{}", e.getMessage());     // 这也是"预期内"的错，warn 一行就够
        return ResponseEntity.status(404)
                .body(Result.fail(ResultCode.NOT_FOUND, e.getMessage()));
    }

    /**
     * ⑤ 【兜底】所有没被上面接住的异常
     *
     * ⭐ 必须放在最后 —— 就是你自己总结的那句"**优先走更具体的 handler**"。
     *    Spring 选 handler 时是"最具体优先"：
     *    ParamException / NoResourceFoundException  →  BaseException  →  Exception
     *
     * 两条铁律（都在这个方法里体现了）：
     *   ① 日志要**详细**：log.error 把异常对象当最后一个参数传 → 完整堆栈进日志
     *   ② 返回要**笼统**：不能把 e.getMessage() 给前端（会泄露类名、方法名、字段名）
     *
     * 验证：GET /sku/boom → 500 + 统一 JSON；
     *      日志里有 ERROR + 完整堆栈，但响应体里**看不到** NullPointerException 字样。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleOther(Exception e) {
        log.error("系统异常", e);                       // ← 完整堆栈
        return ResponseEntity.status(500)
                .body(Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试"));
    }
}
