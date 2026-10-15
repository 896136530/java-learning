package com.xiaoming.day33.web.filter;

import com.xiaoming.day33.web.trace.TraceRecorder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ⭐⭐ 过滤器注册处 —— 今天"两个过滤器谁先谁后"就看这个文件
 *
 * 【本类索引：2 个 @Bean（各注册一个过滤器）】
 *   ┌────────────────────┬──────────┬────────────────────────────────────────┐
 *   │ 过滤器              │ setOrder │ 作用                                    │
 *   ├────────────────────┼──────────┼────────────────────────────────────────┤
 *   │ OrderProbeFilter   │ **1**    │ 进出各记一笔（探针）                     │
 *   │ CostFilter         │ **2**    │ 统计请求耗时（TODO⑥ 要写的）            │
 *   └────────────────────┴──────────┴────────────────────────────────────────┘
 *
 * 【⭐ setOrder 的数字是什么意思？】
 *   **数字越小，越先进入、越后退出**（先进后出的"包"结构）。
 *   按上面的配置，轨迹里应该是：
 *     Filter① 进入 → Filter② 进入 → ……业务…… → Filter② 退出 → Filter① 退出
 *   📌 如果两个过滤器都不设 order：走的是"注册顺序"，但**不要依赖默认值**
 *      （Spring 会给你一个 LOWEST_PRECEDENCE，一旦别处也注册了过滤器，顺序就说不清了）。
 *
 * 【注册过滤器的两种方式（面试可能问）】
 *   ┌──────────────────────────────┬──────────────────────────────────────────────┐
 *   │ ① @WebFilter("/*") +         │ 写在过滤器类上，简单；                         │
 *   │    启动类 @ServletComponentScan│ ⚠️ 但**顺序不好控**（要另想办法），             │
 *   │                              │ 而且过滤器不是 Spring Bean 时**注入不到依赖**  │
 *   ├──────────────────────────────┼──────────────────────────────────────────────┤
 *   │ ② FilterRegistrationBean     │ 写在配置类里，啰嗦一点；                       │
 *   │    （本项目用的）             │ ✅ 能 setOrder / setName / addUrlPatterns，   │
 *   │                              │    能精确控制，生产环境主流做法                │
 *   └──────────────────────────────┴──────────────────────────────────────────────┘
 *   ⭐ 本项目选 ②：因为今天要**看见顺序**，顺序不可控的写法不适合教学。
 *
 * 【⚠️ 一个必须知道的坑：别让过滤器"注册两遍"】
 *   下面两个 Filter 类**故意没有 @Component**。
 *   如果给它们加上 @Component，SpringBoot 会发现"这是一个 Filter Bean" → **自动再注册一次**
 *   （默认拦 /*）→ 加上本类里的 FilterRegistrationBean → **同一个过滤器跑两遍**，
 *   轨迹里就会出现两组"进入/退出"。
 *   📌 规律：**用 FilterRegistrationBean 注册时，过滤器类本身不要是 Spring Bean**；
 *      依赖（这里是 TraceRecorder）直接 `new` 的时候传进去就行。
 */
@Configuration
public class FilterConfig {

    /** 过滤器①：顺序探针（order = 1 → 最先进入、最后退出） */
    @Bean
    public FilterRegistrationBean<OrderProbeFilter> orderProbeFilter(TraceRecorder recorder) {
        // ⭐ 注意：这里是 new 出来的（不是 Spring Bean），依赖靠构造器手动传进去
        FilterRegistrationBean<OrderProbeFilter> registration =
                new FilterRegistrationBean<>(new OrderProbeFilter(recorder));
        registration.addUrlPatterns("/*");     // 拦所有路径
        registration.setName("orderProbeFilter");
        registration.setOrder(1);              // ⭐ 数字越小越先执行
        return registration;
    }

    /** 过滤器②：耗时统计（order = 2 → 后进入、先退出） */
    @Bean
    public FilterRegistrationBean<CostFilter> costFilter(TraceRecorder recorder) {
        FilterRegistrationBean<CostFilter> registration =
                new FilterRegistrationBean<>(new CostFilter(recorder));
        registration.addUrlPatterns("/*");
        registration.setName("costFilter");
        registration.setOrder(2);
        return registration;
    }
}