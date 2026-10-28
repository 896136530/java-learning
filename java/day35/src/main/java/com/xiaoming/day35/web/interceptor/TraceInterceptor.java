package com.xiaoming.day35.web.interceptor;

import com.xiaoming.day35.web.trace.TraceRecorder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * ⭐⭐ 拦截器①：轨迹记录 —— 三个方法（preHandle / postHandle / afterCompletion）都在这里
 *
 * 【本类索引：3 个方法（Day33 的 2 处 TODO 都已修好）】
 *   ┌──────────────────────┬──────────────────────────┬──────────────────────────────┐
 *   │ preHandle            │ Controller **之前**       │ ✅ 已写好：记开始时间 + 记轨迹 │
 *   │ postHandle           │ Controller 之后、渲染之前 │ ✅ 记一行轨迹（Day33 TODO① 已修）│
 *   │ afterCompletion      │ 整个请求**结束后**        │ ✅ 记轨迹 + 存档（清理在别的文件）│
 *   └──────────────────────┴──────────────────────────┴──────────────────────────────┘
 *
 * 【⚠️⚠️ 为什么本类是 @Component、而 AuthInterceptor 不是？（今天最重要的一处对照）】
 *   本类有依赖（TraceRecorder），所以它必须是 Spring Bean 才能"注入依赖"；
 *   而 AuthInterceptor 交付给你时是 `new` 出来的 → 它的 @Autowired 字段**永远是 null**。
 *   两相对照，你就能记住那条铁律：
 *   > **拦截器想注入 Bean，自己就必须先是 Spring Bean**（@Component + 构造器注入），
 *   > 然后在 WebMvcConfig 里**注入进来用**，不要 `new`。
 *   📌 注意：@Component 只是"它是 Bean"，**不会自动生效** —— 还必须
 *      `registry.addInterceptor(...)` 注册（这点和过滤器不一样，过滤器 Bean 会被自动注册）。
 *
 * 【⭐ 为什么拦截器里不能把"开始时间"存成字段？】
 *   因为**拦截器是单例**（Spring 只 new 一个，所有请求共用）。
 *   存成字段 → 请求 A 的时间会被请求 B 覆盖 → 算出来的耗时乱七八糟（Day18/19 的并发问题）。
 *   两个正确做法：
 *     ① `request.setAttribute(name, value)` ← 本类用的：把数据挂在"这一次请求"身上，请求结束就没了
 *     ② ThreadLocal（就是 CurrentUser 的做法）
 *   ⭐ `setAttribute` 是 Servlet 里最常用的"请求级存储"，新东西，记一下：
 *      `request.setAttribute("k", v)` 存、`request.getAttribute("k")` 取（返回 Object，要强转）。
 */
@Component
public class TraceInterceptor implements HandlerInterceptor {

    /** 开始时间存在 request attribute 里的 key（用常量，避免手打字符串写错） */
    private static final String START_ATTR = "traceStartMs";

    private final TraceRecorder recorder;

    public TraceInterceptor(TraceRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * Controller **执行之前**。
     * 返回 true → 放行；返回 false → 中断（Controller 不执行，后面的拦截器也不再执行）。
     *
     * ⭐ 第三个参数 handler 是今天的新东西：
     *    普通接口时它是 **HandlerMethod**（能拿到 Controller 的类、方法、方法上的注解！）
     *    访问不存在的网址时它是 **ResourceHttpRequestHandler**（静态资源的处理器）
     *    把它的类名记进轨迹，Day33 第 2 步观察"/abc 走不走拦截器"时就是证据。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ⭐ 记开始时间（挂在这"一次请求"身上，不能存成字段 —— 拦截器是单例）
        request.setAttribute(START_ATTR, System.currentTimeMillis());
        recorder.record("[拦截器 Trace] preHandle（handler=" + handler.getClass().getSimpleName() + "）");
        return true;
    }

    /**
     * Controller 执行完、**视图渲染之前**。
     *
     * ⚠️ 我们全程是 @RestController（所有接口都带 @ResponseBody）→ **没有视图**，
     *    所以这个方法对我们的业务基本没用（想改返回值也改不了）。
     *    但它**确实会执行**（轨迹里应该看得到）——
     *    所以 Day33 要求你把它补全，好让它出现在轨迹里当"路标"（已补，见下面一行）。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {

        // ✅ Day33 TODO① 的答案就是下面这一行：postHandle 里补一句轨迹，
        //    这样轨迹里 preHandle 后面不会再"直接跳到" afterCompletion。
        //    ⭐ 顺便看第四个参数 ModelAndView：我们是 REST 接口，它是 **null**。
        recorder.record("[拦截器 Trace] postHandle");
    }

    /**
     * ⭐⭐ **请求彻底结束之后**（响应已经发出去了）—— 不管成功还是抛异常，**都会执行**。
     *
     * 所以它是"收尾工作"的指定地点：
     *   ① 统计（算耗时）  ② 写日志  ③ **清理 ThreadLocal（最重要）**
     *
     * 【第四个参数 Exception ex 有个坑（真机实测过）】
     *   你以为"接口抛异常了，这里能拿到"——并不是。
     *   如果异常已经被 GlobalExceptionHandler 处理掉，DispatcherServlet 传下来的是 **null**！
     *   实测：`GET /sku/boom`（NPE → 兜底 handler → 500），本方法的 ex 就是 null。
     *   📌 结论：**想复盘异常，看日志（ERROR + 堆栈），别指望 afterCompletion 的 ex。**
     *
     * 【为什么"耗时"要在这里算？】
     *   只有这里才是"整个请求真的跑完了"的时刻 —— 包括响应写出、异常处理之后的收尾时间。
     *   （这也是和 CostFilter 那个耗时统计的细微区别：Filter 的计时**包得更外一层**，
     *     它会比拦截器多算"DispatcherServlet 之外"的那点开销。）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        Object start = request.getAttribute(START_ATTR);
        long cost = (start == null) ? -1L : (System.currentTimeMillis() - (Long) start);

        recorder.record("[拦截器 Trace] afterCompletion（耗时 " + cost + " ms，异常："
                + (ex == null ? "无" : ex.getClass().getSimpleName()) + "）");

        // 把这次请求的轨迹存进历史，好让 /trace/last 看到"上一次完整的轨迹"
        // ⭐ 注意存档的时刻：此时轨迹里已经有 过滤器的进入 + preHandle + postHandle + 自己，
        //    但还**没有**过滤器的"退出/耗时"（那几行在 chain.doFilter 之后才记）→ 正常现象。
        recorder.publish(request.getMethod(), request.getRequestURI(), response.getStatus(), cost);

        // ⭐ 收尾工作（清 ThreadLocal）**不写在这里** —— 要清的两个 ThreadLocal 位置不同：
        //    ① 轨迹 → 最外层过滤器 OrderProbeFilter 的最后一行（它才是"最后跑完的人"）
        //    ② 当前登录用户 → AuthInterceptor.afterCompletion
        //    （Day33 题目第 7 步有真机实测对照：为什么不能就地清掉①）
    }
}