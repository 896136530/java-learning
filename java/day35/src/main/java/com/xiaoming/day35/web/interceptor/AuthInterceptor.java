package com.xiaoming.day35.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoming.day35.auth.CurrentUser;
import com.xiaoming.day35.auth.TokenStore;
import com.xiaoming.day35.common.Result;
import com.xiaoming.day35.common.ResultCode;
import com.xiaoming.day35.web.trace.TraceRecorder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ⭐⭐ 拦截器②：登录校验（Day33 的核心作业，3 处 TODO 都已经修好）
 *
 * 【本类索引：1 个 preHandle + 1 个 afterCompletion（Day33 的 3 处 TODO 都已修好）】
 *   ┌──────────┬──────────────────────────────────────────────────────────────┐
 *   │ 读 token │ `X-Token` 请求头（没有 → 401）                                 │
 *   │ 验 token │ `tokenStore.findUsername(token)` 换不到用户名 → 401            │
 *   │ 放行      │ `CurrentUser.set(username)` 存进 ThreadLocal，返回 true        │
 *   │ ✅ Day33 TODO② │ 401 写**统一 JSON 信封**（已修好，代码就在 preHandle 里）    │
 *   │ ✅ Day33 TODO③ │ 注入 `TokenStore`（改造成 Spring Bean，已修好）                │
 *   │ ✅ Day33 TODO⑤ │ `afterCompletion` 里清 CurrentUser（ThreadLocal，已修好）     │
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
 *   ① **`@Autowired` 注入不到** → 因为拦截器是 `new` 出来的，不是 Spring Bean（Day33 题目第 5 步）
 *   ② **401 只 setStatus、不写响应体** → 前端收到空白响应，解析 JSON 直接报错（Day33 题目第 4 步）
 *   ③ **白名单写错**（比如 `/api/login` 实际是 `/login`）→ 登录接口自己被拦，谁都登不进去
 *      （Day33 题目第 6 步练过；📌 写路径时**一定回到 Controller 的 @RequestMapping 上抄**，
 *       不要凭记忆写）
 *
 * 【✅ 本文件已经是 Day33 的答案版】
 *   `tokenStore` 是**构造器注入**进来的 Bean，能用；`afterCompletion` 也已经写好。
 *   ⭐ 想复习"为什么 `new` 出来的拦截器注入不到 Bean"，去翻 Day33 的题目第 5 步。
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
     *   📌 这就是"拦截器里 @Autowired 全都是 null"的根因（Day33 题目第 5 步修的就是它）。
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




            // ✅ Day33 TODO② 的答案就是上面那 3 行：
            //    setStatus(401) + setContentType("application/json;charset=UTF-8") + 写出 JSON 信封。
            //    ⚠️ 忘了 charset=UTF-8 → 中文乱码（Day33 真机实测过）。
            //    为什么要返回和别的接口**同样形状**的 JSON？——前端只写一套解析逻辑。


            return false;      // ⭐ false = 拦死：Controller 不执行，后面的拦截器也不执行
        }

        // ---------- 情况②：带了 token，去仓库里验 ----------
        // ✅ tokenStore 是注入进来的 Bean（Day33 TODO③ 已修）——
        //    万一没注入成功，这行会抛 NullPointerException → 被兜底成 500 + "服务器开小差了"。
        String username = tokenStore.findUsername(token);

        if (username == null) {
            log.warn("[Auth] 拒绝：token 无效（{}）", token);
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> body = Result.fail(ResultCode.UNAUTHORIZED, "未登录或登录已过期，请在请求头带上 X-Token");
            response.getWriter().write(objectMapper.writeValueAsString(body));
            // ✅ 两个 401 出口（没带 token / token 无效）都要写 JSON 信封。
            return false;
        }
        


        // ---------- 情况③：验证通过 ----------
        log.info("[Auth] 放行：{} ← 登录用户 {}", uri, username);

        // ⭐ 把"当前登录用户"存进 ThreadLocal —— 后面 Controller / Service 谁要用谁自己取
        CurrentUser.set(username);
        return true;
    }

    /**
     * ⭐⭐ **请求彻底结束之后**（响应已经发出去了）—— 不管成功还是抛异常，**都会执行**。
     *
     * ✅ Day33 TODO⑤ 的答案就在这里：清理 `CurrentUser`（ThreadLocal）。
     *    ⭐ 谁 set 谁 clear —— 不清就是"上一个用户的身份泄漏给下一个请求"的越权事故。
     *
     * ⚠️ 一个反直觉的点：如果 preHandle 返回 false（401 被拒），
     *    **本拦截器自己的 afterCompletion 不会被调用**（Spring 只回调"已经成功通过"的那些）。
     *    但那种情况下 CurrentUser 本来也没 set，所以没有泄漏。
     */
    @Override 
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception ex) {
        CurrentUser.clear();      // ⭐ 谁 set 谁 clear —— 不清就是越权事故
    }

}
