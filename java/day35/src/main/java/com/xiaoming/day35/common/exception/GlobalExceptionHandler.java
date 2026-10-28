package com.xiaoming.day35.common.exception;

import com.xiaoming.day35.common.Result;
import com.xiaoming.day35.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.Map;

/**
 * 全局异常处理器 —— Day32 的**答案版**（今天一字不改地继承下来），Day35 有一个 TODO④ 要加在这里
 *
 * ⭐ 今天这一处改动，就是 Day32「校验三层表」第 ③ 层那句
 *    「有问题就是 500（Day35 换 MySQL 后讲）」的答案 —— 债主就是本文件。
 *
 * 【本类索引：**现在**是 5 个 handler，做完 TODO④ 会变成 6 个】
 *   ┌──────┬──────────────────────────────────┬────────┬──────────────────────────────┐
 *   │ 编号 │ 接住的异常                        │ 状态码 │ 什么时候触发                  │
 *   ├──────┼──────────────────────────────────┼────────┼──────────────────────────────┤
 *   │ ①    │ BaseException（我们自己的异常父类）│ 看异常 │ Service 里 throw              │
 *   │ ②    │ MethodArgumentNotValidException  │ 400    │ **请求体**里的字段校验失败     │
 *   │ ③    │ NoResourceFoundException         │ 404    │ 网址写错                      │
 *   │ ④    │ ConstraintViolationException     │ 400    │ **方法参数**校验失败 ?limit=0  │
 *   │ ⑤    │ Exception（兜底，必须放最后）      │ 500    │ 任何没预料到的 bug            │
 *   └──────┴──────────────────────────────────┴────────┴──────────────────────────────┘
 *   ⭐ TODO④ 加完之后：新的那个是「DuplicateKeyException → 409」，
 *      而原来的兜底 ⑤ 会**顺延成 ⑥**（因为表格是按"从上到下谁先接住"排的）——
 *      ⚠️ 所以本类里的「⑤」「⑥」这两个编号，交付版和答案版**不一样**，看代码时别混。
 *
 * ⚠️⚠️ 编号冲突提醒：Day34 的 `order/` 包里也有一套自己的「TODO①~⑥」（那是**事务**那道题的，
 *    继承过来的、今天不用动）。本文件里的「TODO④」「⑤」「⑥」属于 **Day35 换库那道题**，
 *    以 `java/day35/练习题和代码题目.md` 的九步为准。看到同一个符号别串题。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * ═══════════════════════════════════════════════════════════════════════════
     *  ⭐⭐⭐ Day35 的看点（第 6 步 · 本包最值钱的一个 TODO）：
     *  上面 5 个 handler，== 哪一个都接不住 DuplicateKeyException ==
     * ═══════════════════════════════════════════════════════════════════════════
     *
     * 【它是什么？】
     *   `org.springframework.dao.DuplicateKeyException`
     *   —— 数据库的**唯一索引/唯一约束**被违反时，Spring 把驱动抛的
     *      `java.sql.SQLIntegrityConstraintViolationException` **翻译**成它。
     *   （真机实测的原始报错：`Duplicate entry 'SKU-0001' for key 'sku.uk_sku_code'`，
     *     错误码 1062、SQLSTATE 23000 —— 见题目第 6 步表格。）
     *
     * 【为什么现在没人接它？】
     *   继承链：DuplicateKeyException → DataIntegrityViolationException
     *           → NonTransientDataAccessException → DataAccessException
     *           → RuntimeException → Exception
     *   它 **不是** BaseException 的子类 → ①②③④ 全不匹配
     *   → 掉到 ⑤ 兜底 → **500「服务器开小差了，请稍后重试」**
     *
     * 【这就是 Day32 欠下的那笔债】
     *   `java/day32/知识点.md` 第 542 行那张"校验三层"表，第 ③ 层原话是：
     *     "数据库约束（唯一索引 / NOT NULL / 外键）→ 全局、最终
     *      → 并发下的重复插入 → 走不到 —— 有问题就是 500（Day35 换 MySQL 后讲）"
     *   ⭐ 今天兑现：**你亲手把这个 500 打出来，再把它改成 409。**
     *
     * 【TODO④（第 6 步）要写的东西】
     *   一个和下面 ① 差不多形状的 handler，接住 DuplicateKeyException：
     *     · 状态码 **409**（不是 500！—— 参数没错、服务器也没炸，是"和现有数据冲突了"）
     *     · message 给**人话**（`ResultCode.BIZ_CONFLICT` 的默认文案就是"业务规则冲突"）
     *     · 日志用 **warn**（这是"预期内的冲突"，不是系统故障 —— Day31 学的
     *       "预期内的错 warn 一行 / 真 bug 才 error + 堆栈"）
     *     · ⚠️ **别把 e.getMessage() 直接返回给前端**：那里面带着
     *       `for key 'sku.uk_sku_code'` —— 等于把**表名和索引名**告诉了外部
     *       （Day31 那条"500 不能泄露类名"的同款理由）。
     *       想给用户看编码，用他**自己传上来的那个 code**去拼一句话。
     *
     * 📌 一个"看不出区别"的真相：改完之后，**同一个重复请求的响应体长得不一样了，
     *    但数据库那边其实一模一样** —— 两种情况下那行数据都**没插进去**（唯一索引挡住了）。
     *    ⭐ 所以别把"数据库拦住了"和"接口返回 409"混为一谈：
     *      前者是**数据正确性**（唯一索引的功劳，Day32 第③层）
     *      后者是**用户体验**（handler 的功劳，今天补上）
     */

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
     * ⭐⭐ TODO④（第 6 步）：**在这里（⑤ 兜底的前面）插入一个新的 handler**，
     *   接住 `org.springframework.dao.DuplicateKeyException`（唯一索引冲突）
     *
     *   形状照抄下面 ① 的那个方法（ResponseEntity + log + Result.fail），
     *   要点只有四个：状态码 **409** / 日志 **warn** / message 用**人话** /
     *   **别把 e.getMessage() 发给前端**（里面有表名索引名）。详见类头上那段注释。
     *
     *   ┌─ 你要写的东西（自己写，别抄；提示到这一步就够了）────────────────────────┐
     *   │ @ExceptionHandler(DuplicateKeyException.class)                          │
     *   │ public ResponseEntity<Result<Void>> handleDuplicateKey(DuplicateKeyException e) {│
     *   │     log.warn("唯一约束冲突：{}", e.getMessage());   // ← warn，不是 error      │
     *   │     return ResponseEntity.status(409)                                    │
     *   │             .body(Result.fail(ResultCode.BIZ_CONFLICT, "……"));           │
     *   │ }                                                                        │
     *   └──────────────────────────────────────────────────────────────────────────┘
     *   ⚠️ 别忘了 `import org.springframework.dao.DuplicateKeyException;`
     *      （在 `spring-tx` / `spring-jdbc` 里，不是 `java.sql` 那个。
     *        区分方法：`java.sql.SQLIntegrityConstraintViolationException` 是**驱动原样抛的**，
     *        `DuplicateKeyException` 是 **Spring 翻译过的** —— 我们要接的是后者。）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Result<Void>> handleDuplicateKey(DuplicateKeyException e) {
        log.warn("唯一约束冲突：{}", e.getMessage());   // ← warn，不是 error
        return ResponseEntity.status(409)
                .body(Result.fail(ResultCode.BIZ_CONFLICT, "数据已存在，请勿重复提交"));
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
     *    —— 这就是"拦截器里注入的 Bean 是 null"（就是 Day33 题目第 5 步修的东西）。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleOther(Exception e) {
        log.error("系统异常", e);                       // ← 完整堆栈
        return ResponseEntity.status(500)
                .body(Result.fail(ResultCode.SYSTEM_ERROR, "服务器开小差了，请稍后重试"));
    }
}