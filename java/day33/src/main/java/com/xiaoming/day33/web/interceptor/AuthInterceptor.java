package com.xiaoming.day33.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoming.day33.auth.CurrentUser;
import com.xiaoming.day33.auth.TokenStore;
import com.xiaoming.day33.common.Result;
import com.xiaoming.day33.common.ResultCode;
import com.xiaoming.day33.web.trace.TraceRecorder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ⭐⭐ 拦截器②：登录校验 —— **今天的核心作业**（3 处 TODO 都在这个文件/它的注册处）
 *
 * 【本类索引：1 个 preHandle + 3 个 TODO】
 *   ┌──────────┬──────────────────────────────────────────────────────────────┐
 *   │ 读 token │ `X-Token` 请求头（没有 → 401）                                 │
 *   │ 验 token │ `tokenStore.findUsername(token)` 换不到用户名 → 401            │
 *   │ 放行      │ `CurrentUser.set(username)` 存进 ThreadLocal，返回 true        │
 *   │ TODO②    │ 第 4 步：401 要写**统一 JSON 信封**，不能只有状态码             │
 *   │ TODO③    │ 第 5 步：修"注入的 Bean 是 null"（改造成 Spring Bean）           │
 *   │ TODO⑤    │ 第 7 步：补 `afterCompletion`，清 CurrentUser（ThreadLocal）    │
 *   └──────────┴──────────────────────────────────────────────────────────────┘
 *
 * 【⭐ 为什么 token 放在自定义头 X-Token 里，而不是 Authorization？】
 *   标准做法是 `Authorization: Bearer <token>`（要按格式切成两半）。
 *   今天用自定义头是为了**少一层解析**，让注意力留在拦截器本身。
 *   📌 真实项目用标准头更好（网关、Swagger、前端库都认它）。
 *   ⚠️ 如果以后要跨域（CORS），自定义头必须在服务端 `Access-Control-Allow-Headers` 里放行，
 *      否则浏览器会把请求拦掉（这是"接口用 Postman 通、用网页不通"的常见原因）。
 *
 * 【⭐ 三个易错点，全是真机踩过的】
 *   ① **`@Autowired` 注入不到** → 因为拦截器是 `new` 出来的，不是 Spring Bean（题目第 5 步）
 *   ② **401 只 setStatus、不写响应体** → 前端收到空白响应，解析 JSON 直接报错（题目第 4 步）
 *   ③ **白名单写错**（比如 `/api/login` 实际是 `/login`）→ 登录接口自己被拦，谁都登不进去
 *      （题目第 6 步会练；📌 写路径时**一定回到 Controller 的 @RequestMapping 上抄**，
 *       不要凭记忆写）
 *
 * 【⚠️ 本文件交付版是"坏"的：字段没被注入】
 *   下面这个 `tokenStore` 现在**永远是 null** —— 但它一样能编译、能启动，
 *   只在"带 token 访问 /sku"时才炸（`NullPointerException` → 500）。
 *   ⭐ 这正是那句老话：**编译通过 ≠ 注入成功**。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final TokenStore tokenStore;
    private final TraceRecorder recorder;
    private final ObjectMapper objectMapper;

    public AuthInterceptor(TokenStore tokenStore, TraceRecorder recorder, ObjectMapper objectMapper) {
        this.tokenStore = tokenStore;
        this.recorder = recorder;
        this.objectMapper = objectMapper;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    /** 约定：token 放在这个请求头里 */
    public static final String TOKEN_HEADER = "X-Token";
    
    
    /**
     * ⭐ @Autowired：让 Spring 自动把容器里的 TokenStore 塞进来。
     *
     * ⚠️⚠️ 但有个前提：**本类自己必须先是一个 Spring Bean**！
     *   现在它是在 `WebMvcConfig` 里 `new AuthInterceptor()` 出来的，
     *   Spring 根本不知道有这么个对象 → **这个字段永远是 null**。
     *   📌 这就是"拦截器里 @Autowired 全都是 null"的根因（题目第 5 步要修）。
     *   修法不是"再加一个 @Autowired"，而是**让拦截器成为 Spring Bean**
     *   （@Component + 构造器注入），然后在 WebMvcConfig 里注入进来用。
     */


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String uri = request.getMethod() + " " + request.getRequestURI();
        String token = request.getHeader(TOKEN_HEADER);       // 取请求头

        // ---------- 情况①：压根没带 token ----------
        if (token == null || token.isBlank()) {
            log.warn("[Auth] 拒绝：{} 没有带 {} 请求头", uri, TOKEN_HEADER);

            // ⭐ 状态码给 401（不是 400/403，见 UnauthorizedException 的注释）
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.UNAUTHORIZED, "未登录或登录已过期，请在请求头带上 X-Token")));




            // ==================== ⭐ TODO②（第 4 步）：这里少了响应体 ====================
            // 现在客户端只收到一个光秃秃的 401（**0 字节**），前端 `res.json()` 直接报错。
            // 要做的：写一段和别的接口**同样形状**的 JSON 信封：
            //   {
            //     "code" : "401",
            //     "message" : "未登录或登录已过期，请在请求头带上 X-Token",
            //     "data" : null,
            //     "fieldErrors" : null
            //   }
            //
            // 【怎么做？给你三条提示，任选其一（推荐第 3 种）】
            //   ① response.setContentType("application/json;charset=UTF-8");
            //      response.getWriter().write("{\"code\":\"401\",...}");   ← 手拼字符串，容易写错
            //   ② 先 `Result.fail(ResultCode.UNAUTHORIZED, "...")` 造对象，
            //      再用 ObjectMapper（Jackson）转成字符串写出去
            //   ③ ⭐ 最省事：让本类持有 Spring 容器里的 ObjectMapper
            //      （它是一个 Bean，构造器注入进来），然后：
            //        response.setContentType("application/json;charset=UTF-8");
            //        response.getWriter().write(objectMapper.writeValueAsString(body));
            //      （ObjectMapper = Jackson 的"对象 ↔ JSON"转换器，Day30 起一直是它在干活）
            //
            // 📌 不管用哪种，**三件事都要做到**：
            //      setStatus(401) + setContentType 带 charset=UTF-8 + 写出 JSON
            //      ⚠️ 忘了 charset → 中文变乱码（真机实测过）
            // ==========================================================================


            return false;      // ⭐ false = 拦死：Controller 不执行，后面的拦截器也不执行
        }

        // ---------- 情况②：带了 token，去仓库里验 ----------
        // ⚠️ 下面这行在"没修好 TODO③"时会抛 NullPointerException（tokenStore 是 null），
        //    异常会被 GlobalExceptionHandler 兜底 → 500 + "服务器开小差了"。
        String username = tokenStore.findUsername(token);

        if (username == null) {
            log.warn("[Auth] 拒绝：token 无效（{}）", token);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> body = Result.fail(ResultCode.UNAUTHORIZED, "未登录或登录已过期，请在请求头带上 X-Token");
            response.getWriter().write(objectMapper.writeValueAsString(body));
            // ⭐ TODO② 也要在这条分支里写 JSON（两个 401 出口都得写）
            return false;
        }
        


        // ---------- 情况③：验证通过 ----------
        log.info("[Auth] 放行：{} ← 登录用户 {}", uri, username);

        // ⭐ 把"当前登录用户"存进 ThreadLocal —— 后面 Controller / Service 谁要用谁自己取
        CurrentUser.set(username);
        return true;
    }

    /**
     * ⭐ TODO⑤（第 7 步·第 2 处）：本类**还没有 afterCompletion 方法**，请补一个：
     *
     * ```java
     * @Override
     * public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
     *                             Object handler, Exception ex) {
     *     CurrentUser.clear();      // ⭐ 谁 set 谁 clear —— 不清就是越权事故
     * }
     * ```
     * 验证办法：`GET /me`（带 token）之后立刻 `GET /trace/whos` →
     *   有残留（能看到用户名）说明没清；干净（null）说明清对了。
     *   📌 注意：**必须修好 TODO③ 之后才测得到**（否则请求根本走不到 preHandle 的成功分支）。
     *
     * ⚠️ 顺便理解一个反直觉的点：如果 preHandle 返回了 false（401 被拒），
     *   **本拦截器自己的 afterCompletion 不会被调用**（Spring 只回调"已经成功通过"的那些）。
     *   但那种情况下 CurrentUser 本来也没 set，所以没有泄漏。
     */
    @Override 
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) {
        CurrentUser.clear();      // ⭐ 谁 set 谁 clear —— 不清就是越权事故
    }

}
