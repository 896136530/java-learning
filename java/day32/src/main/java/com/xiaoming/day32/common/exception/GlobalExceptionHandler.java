package com.xiaoming.day32.common.exception;

import com.xiaoming.day32.common.Result;
import com.xiaoming.day32.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.Map;

/**
 * 全局异常处理器 —— Day31 那份**原样搬过来** + 第 7 步补的一条 handler
 *
 * 【本类索引：5 个 handler（①②③⑤ + ④兜底），一张表看清】
 *   ┌──────┬──────────────────────────────────┬────────┬──────────────────────────────┐
 *   │ 编号 │ 接住的异常                        │ 状态码 │ 什么时候触发                  │
 *   ├──────┼──────────────────────────────────┼────────┼──────────────────────────────┤
 *   │ ①    │ BaseException（我们自己的异常父类）│ 看异常 │ Service 里 throw              │
 *   │ ②    │ MethodArgumentNotValidException  │ 400    │ **请求体**里的字段校验失败     │
 *   │ ③    │ NoResourceFoundException         │ 404    │ 网址写错                      │
 *   │ ⑤    │ ConstraintViolationException     │ 400    │ **方法参数**校验失败(?limit=0) │
 *   │ ④    │ Exception（兜底，必须放最后）      │ 500    │ 任何没预料到的 bug            │
 *   └──────┴──────────────────────────────────┴────────┴──────────────────────────────┘
 *
 * 【⭐⭐ 今天最值钱的一句话 —— ② 和 ⑤ 那条**看着都是"参数校验失败"，其实是两个异常**】
 *   ┌────────────┬────────────────────────────────┬──────────────────────────────────┐
 *   │            │ ② 请求体（JSON）里的字段         │ ⑤ 方法参数（?limit=0 这种）       │
 *   ├────────────┼────────────────────────────────┼──────────────────────────────────┤
 *   │ 谁来校验    │ SpringMVC：把 JSON 转成对象时     │ Spring 的 AOP：调用方法**之前**     │
 *   │ 靠什么触发  │ 参数上的 @Valid / @Validated     │ **类上的 @Validated**             │
 *   │ 抛什么异常  │ MethodArgumentNotValidException │ ConstraintViolationException      │
 *   │ 错误从哪取  │ e.getBindingResult().getFieldErrors() │ e.getConstraintViolations()  │
 *   │ 字段名长啥样│ `title`（干净）                  │ `search.limit`（**带方法名前缀**）  │
 *   └────────────┴────────────────────────────────┴──────────────────────────────────┘
 *   ⭐ 同一件事（参数不合法）走了两条完全不同的路 —— 这就是 Spring 里最容易漏接的一类异常。
 *     写 ② 的时候没人会提醒你还有另一条，所以题目第 1 步让你先**亲眼看到**它是 500。
 *
 * 【选 handler 的规则（Day31 学的，复习一下）】
 *   同一个异常有多个 handler 都能处理时，Spring 选**最具体**的那个：
 *     ParamException（子类）→ BaseException（父类）→ Exception（兜底）
 *   所以顺序是：先写具体的，**兜底永远放最后**。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ① 兜住所有自定义业务异常（BaseException 及其子类）—— 404 / 400 / 409 全从这走
     *
     * ⭐ 用 ResponseEntity 而不是直接 return Result：
     *    因为要**同时控制**「HTTP 状态码」和「响应体」。
     *    直接 return Result 只能控制响应体，状态码会是 200（那就退回 Day30 的坑了）。
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBase(BaseException e) {
        // 业务异常是"预期内"的，warn 一行就够（不要打堆栈，太吵）
        log.warn("业务异常：code={}, http={}, message={}", e.getCode(), e.getHttp(), e.getMessage());
        return ResponseEntity.status(e.getHttp())
                .body(Result.fail(ResultCode.ofHttp(e.getHttp()), e.getMessage()));
    }

    /**
     * ② **请求体**参数校验失败（@Valid / @Validated 校验 DTO 时抛的）
     *
     * 什么时候触发？比如：
     *   POST /sku  请求体 {"title":"", "price":-1}
     *   而 SkuForm 上有 @NotBlank / @Min → Spring 抛 MethodArgumentNotValidException
     *
     * 把每个字段的错误**分开收集**成 map：{"title":"书名不能为空", "price":"价格不能小于 0"}
     * → 前端能直接把红字标在对应输入框旁边。
     *
     * ⚠️ 注意：分组校验（OnCreate / OnUpdate）失败时，抛的**还是这个异常**，
     *    因为"用哪一组"是 SpringMVC 解析参数时就决定好的 —— 异常类型不变。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = Result.fieldErrors();
        e.getBindingResult().getFieldErrors().forEach(fe ->
                // getDefaultMessage() 就是注解里写的 message，如 @NotBlank(message="书名不能为空")
                errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        log.warn("请求体校验失败：{}", errors);
        return ResponseEntity.status(400)
                .body(Result.failFields(ResultCode.PARAM_ERROR, "参数校验失败，请看 fieldErrors", errors));
    }

    /**
     * ③ 网址写错了（访问了一个没有对应接口的路径）→ 404
     *
     * ⭐ 为什么必须单独写一条？—— Spring 6 里"网址不存在"抛的是
     *    `NoResourceFoundException`，它**也是 Exception 的子类**，
     *    不单独接的话就会被 ④ 兜底接成 **500**（"服务器炸了"）。
     *    那种后果：用户手抖输错网址 → 监控全站报警"服务器错误"。
     *    （⚡ 这是 Day31 你自己实测发现并修好的坑，记在这儿了。）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("网址不存在：{}", e.getMessage());     // 预期内的错，warn 一行就够
        return ResponseEntity.status(404)
                .body(Result.fail(ResultCode.NOT_FOUND, e.getMessage()));
    }

    /**
     * ⭐ 第 7 步（TODO⑤）：**方法参数**校验失败 → 400 + fieldErrors
     *
     * 触发场景：`GET /sku/search?limit=0` —— limit 上有 @Min(1)，但你传了 0。
     * 它抛的是 `ConstraintViolationException`（走 AOP 那条路，和 ② 的请求体校验不是一回事），
     * 没人接就会掉进下面 ④ 兜底 → 返回 500「服务器开小差了」。
     * 明明是用户传错参数，却报"服务器炸了"，所以要单独给它一条 handler。
     *
     * 三个要点：
     *   ① 状态码 **400**：这是"请求不合法"，不是"服务器出错"
     *   ② 用 `Result.failFields(...)` 装 fieldErrors（和 ② 的格式一致，前端好处理）
     *   ③ ⭐ 字段名要**截断**：`getPropertyPath()` 给的是 `search.limit`（方法名.参数名），
     *      直接丢给前端对应不上输入框 → 取最后一个 `.` 之后的部分
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException e) {
        Map<String, String> errors = Result.fieldErrors();
        for (ConstraintViolation<?> v : e.getConstraintViolations()) {
            String path = v.getPropertyPath().toString();       // 形如 "search.limit"
            int dot = path.lastIndexOf('.');
            String field = (dot >= 0) ? path.substring(dot + 1) : path;   // ⭐ 截成 "limit"
            errors.putIfAbsent(field, v.getMessage());
        }

        log.warn("方法参数校验失败：{}", errors);          // 预期内的错 → warn，不打堆栈
        return ResponseEntity.status(400)
                .body(Result.failFields(ResultCode.PARAM_ERROR, "参数校验失败，请看 fieldErrors", errors));
    }

    /**
     * ④ 【兜底】所有没被上面接住的异常
     *
     * ⭐ 必须放在最后 —— 就是你自己总结的那句"**优先走更具体的 handler**"。
     *
     * 两条铁律（都体现在这个方法里）：
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