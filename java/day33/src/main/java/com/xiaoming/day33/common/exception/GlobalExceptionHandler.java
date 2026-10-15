package com.xiaoming.day33.common.exception;

import com.xiaoming.day33.common.Result;
import com.xiaoming.day33.common.ResultCode;
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
 * 全局异常处理器 —— Day32 的**答案版**（今天一字不改，但今天会拿它做两个实验）
 *
 * 【本类索引：5 个 handler，一张表看清】
 *   ┌──────┬──────────────────────────────────┬────────┬──────────────────────────────┐
 *   │ 编号 │ 接住的异常                        │ 状态码 │ 什么时候触发                  │
 *   ├──────┼──────────────────────────────────┼────────┼──────────────────────────────┤
 *   │ ①    │ BaseException（我们自己的异常父类）│ 看异常 │ Service 里 throw              │
 *   │ ②    │ MethodArgumentNotValidException  │ 400    │ **请求体**里的字段校验失败     │
 *   │ ③    │ NoResourceFoundException         │ 404    │ 网址写错                      │
 *   │ ④    │ ConstraintViolationException     │ 400    │ **方法参数**校验失败 ?limit=0  │
 *   │ ⑤    │ Exception（兜底，必须放最后）      │ 500    │ 任何没预料到的 bug            │
 *   └──────┴──────────────────────────────────┴────────┴──────────────────────────────┘
 *
 * 【⭐⭐ 今天（Day33）要用它做两个实测 —— 都是"拦截器/过滤器和异常处理器的关系"】
 *   实验 A（拦截器里抛异常）：`GET /trace/throw` → ThrowInterceptor.preHandle 里
 *           `throw new ApplicationException(...)` → **走进下面的 ①** → 409 + 统一 JSON。
 *           📌 结论先给你（真机实测见练习题文末）：**拦截器里抛的业务异常，会被统一异常处理器接住**。
 *              因为 preHandle 是在 DispatcherServlet 的 try 块里被调用的。
 *   实验 B（异常时的 afterCompletion）：`GET /sku/boom`（NPE）→ 走 ⑤ 兜底 → 500；
 *           但拦截器的 `afterCompletion(…, Exception ex)` 里那个 **ex 是 null**！
 *           （因为异常已经被这里"消化"了，DispatcherServlet 传下去的是 null。）
 *           👉 这就是"统一异常处理的代价"：**外面那层看不到异常了，想看只能翻日志**。
 *
 * 【选 handler 的规则（Day31 学的）】
 *   同一个异常有多个 handler 都能处理时，Spring 选**最具体**的那个：
 *     ParamException → BaseException → Exception（兜底），所以**兜底永远放最后**。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ① 兜住所有自定义业务异常（BaseException 及其子类）—— 400 / 401 / 404 / 409 全从这走
     *
     * ⭐ 用 ResponseEntity 而不是直接 return Result：
     *    因为要**同时控制**「HTTP 状态码」和「响应体」。
     *
     * ⭐ Day33 的新成员 401（UnauthorizedException）也走这条 ——
     *    一行 handler 代码没改就多支持了一个状态码，这就是开头那个枚举的价值。
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Void>> handleBase(BaseException e) {
        // 业务异常是"预期内"的，warn 一行就够（不要打堆栈，太吵）
        log.warn("业务异常：code={}, http={}, message={}", e.getCode(), e.getHttp(), e.getMessage());
        return ResponseEntity.status(e.getHttp())
                .body(Result.fail(ResultCode.ofHttp(e.getHttp()), e.getMessage()));
    }

    /**
     * ② **请求体**参数校验失败（@Validated(组) 校验 DTO 时抛的）
     * 把每个字段的错误**分开收集**成 map：{"title":"书名不能为空", "price":"价格不能小于 0"}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> errors = Result.fieldErrors();
        e.getBindingResult().getFieldErrors().forEach(fe ->
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
     *    不单独接的话就会被 ⑤ 兜底接成 **500**（"服务器炸了"）。
     *    （⚡ 这是 Day31 你自己实测发现并修好的坑。）
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("网址不存在：{}", e.getMessage());
        return ResponseEntity.status(404)
                .body(Result.fail(ResultCode.NOT_FOUND, e.getMessage()));
    }

    /**
     * ④ **方法参数**校验失败（GET /sku/search?limit=0）
     *
     * ⭐ 字段名要**截断**：`getPropertyPath()` 给的是 `search.limit`（带方法名前缀），
     *    直接丢给前端会对应不上输入框 → 取最后一个 `.` 之后的部分。
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
        log.warn("方法参数校验失败：{}", errors);
        return ResponseEntity.status(400)
                .body(Result.failFields(ResultCode.PARAM_ERROR, "参数校验失败，请看 fieldErrors", errors));
    }

    /**
     * ⑤ 【兜底】所有没被上面接住的异常
     *
     * ⭐ 必须放在最后 —— "优先走更具体的 handler"。
     * 两条铁律：
     *   ① 日志要**详细**：log.error 把异常对象当最后一个参数传 → 完整堆栈进日志
     *   ② 返回要**笼统**：不能把 e.getMessage() 给前端（会泄露类名、方法名、字段名）
     *
     * ⭐ 今天你会在这里看到一条和拦截器有关的记录：
     *    `NullPointerException: Cannot invoke "…TokenStore.findUsername(String)" because "this.tokenStore" is null`
     *    —— 这就是"拦截器里注入的 Bean 是 null"（题目第 5 步要修的东西）。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleOther(Exception e) {
        log.error("系统异常", e);                       // ← 完整堆栈
        return ResponseEntity.status(500)
                .body(Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试"));
    }
}