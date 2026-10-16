package com.xiaoming.day33.web.filter;

import com.xiaoming.day33.web.trace.TraceRecorder;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ⭐ 过滤器②：请求耗时统计 —— **你的第一个"能干活的"过滤器**（题目第 8 步 TODO⑥）
 *
 * 【本类索引：1 个 doFilter（含 2 处 TODO）】
 *   ┌─────────────────────────────┬──────────────────────────────────────────┐
 *   │ recorder.record(进入)       │ ✅ 已经写好                                 │
 *   │ TODO⑥ 开始计时               │ ⬜ 你写：long start = ...                   │
 *   │ chain.doFilter(...)         │ ✅ 已经写好（⭐ 放行，别删）                  │
 *   │ TODO⑥ 算耗时 + 打日志        │ ⬜ 你写：cost = ... ; log.info(...)         │
 *   │ recorder.record(退出)       │ ✅ 已经写好                                 │
 *   └─────────────────────────────┴──────────────────────────────────────────┘
 *
 * 【为什么"耗时统计"是过滤器的经典用例？】
 *   因为它天然要**包住整个请求**：进来时记时间、出去时算差值。
 *   这正是过滤器"先进后出"的结构最擅长的事。
 *   ⭐ 真实项目里同类需求还有：日志埋点、SQL 计时、慢请求报警、限流计数……
 *     它们共同的特点是 **"和业务无关，但每个请求都要做"**。
 *
 * 【⭐ 为什么要强转 ServletRequest → HttpServletRequest？】
 *   接口签名规定参数是 `ServletRequest`（Servlet 规范里 HTTP 只是其中一种协议，
 *   虽然现实中几乎只有 HTTP）。想拿 URI、方法、状态码这些 **HTTP 专有**的东西，
 *   就得强转成 `HttpServletRequest`。
 *   📌 注意：**方法签名不能改成 HttpServletRequest** —— 那就不叫"重写"了（参数类型不许变窄）。
 *
 * 【⚠️ 计时常见的两个错】
 *   ① 把 start 记在 `chain.doFilter` **后面** → 耗时永远是 0（先做了后算账）
 *   ② 用 `System.currentTimeMillis()` 记在 try 之外忘了 finally → 抛异常时"退出"那句不执行
 *      （今天不折腾 finally，正常路径够用；知道有这回事就行）
 */
public class CostFilter implements Filter {

    /** 日志（SpringBoot 里用 log 而不是 System.out.println —— Day30 学过的规矩） */
    private static final Logger log = LoggerFactory.getLogger(CostFilter.class);

    private final TraceRecorder recorder;

    public CostFilter(TraceRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        recorder.record("[Filter② Cost] 进入");

        // ⭐ 想拿 HTTP 专属信息，就得强转（见上面说明）
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // ==================== ⭐ TODO⑥（第 8 步·第 1 处）：开始计时 ====================
        // ✅ 已补（答案版）—— 毫秒时间戳。
        // ⚠️ 计时变量必须是**局部变量**：过滤器是单例，存成 `private long start` 字段
        //    会在并发下串时间（Day18/19 的坑）。
        long start = System.currentTimeMillis();
        // ==========================================================================

        chain.doFilter(request, response);          // ⭐ 放行（这行千万别删）

        // ==================== ⭐ TODO⑥（第 8 步·第 2 处）：算耗时并记录 ====================
        // ✅ 已补（答案版）：三件事 —— ① 算耗时 ② 打日志（用 {} 占位符，不拼字符串）
        //    ③ 顺手写进轨迹（这样"耗时"才会出现在 /trace/last 里）。
        //  📌 httpResponse.getStatus() 在这一刻**已经是最终状态码**了
        //     （响应是走完 framework 才写出去的，但状态码已经 set 好了）
        // ==============================================================================
        long cost = System.currentTimeMillis() - start;

        log.info("[Filter② Cost] {} {} → {}，耗时 {} ms",
                httpRequest.getMethod(), httpRequest.getRequestURI(), httpResponse.getStatus(), cost);
        recorder.record("[Filter② Cost] 耗时 " + cost + " ms");

        recorder.record("[Filter② Cost] 退出");
        // 📌 本过滤器**不负责**清理轨迹 —— 那是最外层 OrderProbeFilter 的活（见那里 TODO⑤ 的说明）
    }
}