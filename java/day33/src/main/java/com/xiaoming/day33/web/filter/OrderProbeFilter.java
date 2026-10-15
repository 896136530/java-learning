package com.xiaoming.day33.web.filter;

import com.xiaoming.day33.web.trace.TraceRecorder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ⭐ 过滤器①：顺序探针 —— 进出各记一笔 + 打一行日志
 *
 * 【这个类就是"过滤器最小骨架"，你要能默写出来】
 *   ```java
 *   public class XxxFilter implements Filter {
 *       public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
 *               throws IOException, ServletException {
 *           前置逻辑...
 *           chain.doFilter(req, res);     // ⭐ 放行（这一行不写，请求就"死"在这了）
 *           后置逻辑...
 *       }
 *   }
 *   ```
 *
 * 【⚠️⭐ 为什么"放行"那一行这么关键？】
 *   `chain.doFilter(request, response)` = "我处理完了，交给下一个"。
 *   不调用它 → 链条到此为止 → **后面的过滤器、DispatcherServlet、Controller 全都不执行**。
 *   浏览器那边看到的是"一直转圈"或者"空白响应"（因为没人写响应体）。
 *   📌 这就是过滤器最大的坑：**它不是"返回 false"，而是"少写一行"** ——
 *      编译器不报错、启动不报错、日志不报错，就是接口全 404/空白。
 *
 * 【和拦截器 preHandle 返回 false 的对比（记住这张表）】
 *   ┌──────────────┬────────────────────────────────────────┐
 *   │ 过滤器"拦死"  │ **不写** `chain.doFilter(...)`           │
 *   │ 拦截器"拦死"  │ `preHandle` **return false**            │
 *   └──────────────┴────────────────────────────────────────┘
 *   一个靠"漏写一行"，一个靠"返回值" —— 这就是两种东西设计风格上的差别。
 *
 * 【生命周期：init / doFilter / destroy】
 *   init()    → 应用启动时**一次**（本类打了日志，启动时能在控制台看到）
 *   doFilter()→ **每个请求**都走一次
 *   destroy() → 应用**正常关闭**时**一次**（Ctrl+C 停止服务时能看到）
 *   ⭐ 记忆：**init 一次、doFilter 无数次、destroy 一次**。
 *
 * 【⭐⭐ 为什么"清理 ThreadLocal"写在本类最后一行，而不是拦截器的 afterCompletion？】
 *   —— 这是本题第 7 步真机实测出来的结论，非常重要：
 *   本过滤器的 order=1（最早进、**最晚出**），所以它的最后一行是**整个请求的最后一句代码**。
 *   而拦截器的 afterCompletion 只是"DispatcherServlet 里的最后一步"，
 *   它执行完之后，控制权还要退回过滤器链 ——
 *   也就是说 `CostFilter` 和本过滤器在 `chain.doFilter` **之后**的那几行还会继续记轨迹。
 *   实测数据（交付版 vs 答案版，见练习题文末）：
 *     把 clear 写在 afterCompletion → 下一次请求（同一个 Tomcat 线程）的"本次请求"里
 *       会带着上一次残留的 3 行：`[Filter② Cost] 耗时 xx ms / [Filter② Cost] 退出 / [Filter①] 退出`
 *     把 clear 写在**本类最后一行** → 干净（本次请求恰好 3 行）
 *   📌 口诀：**谁最后动这个数据，谁负责清理** —— 不是"名字里带 after 的方法就一定能收尾"。
 */
public class OrderProbeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(OrderProbeFilter.class);

    /** 依赖是"手动传进来"的（因为本类不是 Spring Bean，见 FilterConfig） */
    private final TraceRecorder recorder;

    public OrderProbeFilter(TraceRecorder recorder) {
        this.recorder = recorder;
    }

    /** 应用启动时调用一次 */
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("[Filter① OrderProbe] init() —— 过滤器初始化（整个应用只调一次）");
    }

    /** 每个请求都调用一次 */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        recorder.record("[Filter① OrderProbe] 进入");
        log.info("[Filter① OrderProbe] 进入 {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

        // ⭐⭐ 放行：这一行是过滤器的命根子，删掉 = 后面所有代码都执行不到
        chain.doFilter(request, response);

        // ⭐ 注意：下面这几行在 chain.doFilter **之后** → 所以它们**最后**执行
        //    （Filter 是"包"住整个请求的：先进后出）
        recorder.record("[Filter① OrderProbe] 退出");
        log.info("[Filter① OrderProbe] 退出 {}", httpRequest.getRequestURI());

        // ==================== ⭐ TODO⑤（第 7 步·第 1 处）：清柜子 ====================
        // 请在这里补上**一行**：
        //     recorder.clear();
        //
        // ⭐ 为什么清在这一层、而不是拦截器的 afterCompletion 里？
        //    因为本类是 order=1（最早进、最晚出），上面那两行是**整个请求的最后两句**；
        //    拦截器 afterCompletion 跑完之后，CostFilter 和本过滤器还会继续记轨迹 ——
        //    在 afterCompletion 里清，等于"扫完地又有人进来踩了两脚"（实测会残留 3 行）。
        //    （更详细的实测对照见 `练习题和代码题目.md` 第 7 步）
        // ==========================================================================
    }

    /** 应用正常关闭时调用一次 */
    @Override
    public void destroy() {
        log.info("[Filter① OrderProbe] destroy() —— 过滤器销毁（应用正常关闭时才调）");
    }
}