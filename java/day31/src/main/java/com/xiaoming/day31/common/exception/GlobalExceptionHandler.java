package com.xiaoming.day31.common.exception;

import com.xiaoming.day31.common.Result;
import com.xiaoming.day31.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
     * ③ TODO①（你写）：兜底处理 —— 拦住所有"没预料到"的异常
     *
     * 为什么必须有它？——
     *   · 空指针、数组越界、除零……这类异常如果没人处理，
     *     Spring 会返回它自带的 Whitelabel 错误页（HTML！），前端根本没法解析。
     *   · 而且**堆栈信息会暴露给外面**（表名、类名、甚至密码），这是安全问题。
     *
     * 你要做的：
     *   写一个方法，处理 `Exception.class`（所有异常的最终兜底），要求：
     *     ① 注解：@ExceptionHandler(Exception.class)
     *     ② **把完整堆栈写进日志**：log.error("系统异常", e);
     *        ⭐ 和上面 ①② 的区别：前两个是"预期内的"，只记一行；这个是"出 bug 了"，
     *           **必须打完整堆栈**，否则线上排查无从下手。
     *     ③ 返回 500 + 统一格式，但**消息只能给一句笼统的**：
     *        Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试")
     *        ⚠️ 绝对不能把 e.getMessage() 返回给前端（会泄露内部实现）
     *
     * 参考写法（照 ① 改就行）：
     *
     *   @ExceptionHandler(Exception.class)
     *   public ResponseEntity<Result<Void>> handleOther(Exception e) {
     *       log.error("系统异常", e);                       // ← 完整堆栈
     *       return ResponseEntity.status(500)
     *               .body(Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试"));
     *   }
     *
     * 验证：访问 GET /sku/boom （这个接口故意抛空指针）→ 应该返回 500 + 上面的 JSON
     *       （不写这个方法的话，你会看到 SpringBoot 自带的 HTML 错误页）
     */
    // ← 在这里写 TODO① 的方法
}