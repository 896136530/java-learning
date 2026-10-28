package com.xiaoming.day35.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ⭐ 拦截器注册处（Day33 的成果，今天**只改了一处：白名单**）
 *
 * 【本类索引：3 条注册】
 *   ┌──────┬──────────────────────┬────────────────────────┬────────────────────────────────┐
 *   │ 顺序 │ 拦截器                │ 拦截路径                │ 排除（白名单）                  │
 *   ├──────┼──────────────────────┼────────────────────────┼────────────────────────────────┤
 *   │ ①    │ TraceInterceptor     │ `/**`（全部）           │ 无（要观察所有请求）            │
 *   │ ②    │ AuthInterceptor      │ `/**` + 白名单          │ /login /public/** /trace/**    │
 *   │      │                      │                        │ /db/** /order/** /h2-console/** │
 *   │ ③    │ ThrowInterceptor     │ `/trace/throw/**`      │ 无（实验用）                    │
 *   └──────┴──────────────────────┴────────────────────────┴────────────────────────────────┘
 *
 * 【⭐ 今天为什么动了白名单？（这是一次有意的"减少变量"）】
 *   Day33 的规矩是"业务接口都要带 X-Token"。今天要把 `/order/**`（下单）和 `/db/**`（查库）
 *   放进白名单，理由只有一条：**今天的实验变量已经够多了**。
 *   事务实验要做的是"复位 → 打一个接口 → 查库"，如果每次还要先登录拿 token、
 *   一旦忘了带就收到 401，你会分不清"没回滚"和"没登录"。
 *   📌 真实项目里下单接口当然必须登录 —— 这属于"为了看清一个现象，暂时挪开无关变量"，
 *      和 Day31/32/33 "刻意先不用数据库"是同一个手法。
 *
 * 【⭐⭐ 顺便记住一件事：拦截器和事务拦截器**不是一回事**】
 *   这里的 `AuthInterceptor` 是 **Spring MVC 的拦截器**，它在 **DispatcherServlet 内部**执行；
 *   而 `@Transactional` 的拦截器（`TransactionInterceptor`）是 **Spring AOP 的**，
 *   它执行的位置**更靠里**（在 Controller 调用 Service 的那一刻）。
 *   实测对比：拦截器里注入的 Bean 是 null 时，连事务都谈不上（Day33 那个坑）。
 *   一句话顺序：**Filter → Interceptor → DispatcherServlet → 【事务拦截器】→ Service 方法**。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * ⭐ 不需要登录的路径（白名单）
     * 每一条都要能对上某个 Controller 的 @RequestMapping，不然就是白写。
     */
    private static final String[] WHITE_LIST = {
            "/login",           // AuthController：不然永远登不进去
            "/public/**",       // PublicController：免登录的探活/公告
            "/trace/**",        // TraceController：调试窗口（不加这条你会把自己关在门外）
            "/db/**",           // ⭐ Day34 新增：查库窗口（今天的"验尸报告"）
            "/order/**",        // ⭐ Day34 新增：下单接口（今天的主角，专心做事务实验）
            "/h2-console/**",   // ⭐ Day34 新增：H2 网页控制台
            "/favicon.ico"      // Day33 留下的小副作用（不加它浏览器图标会被拦成 401）
    };

    private final TraceInterceptor traceInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(TraceInterceptor traceInterceptor, AuthInterceptor authInterceptor) {
        this.traceInterceptor = traceInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // ==================== ① 轨迹记录：拦所有请求 ====================
        registry.addInterceptor(traceInterceptor)
                .addPathPatterns("/**");

        // ==================== ② 登录校验：全拦 + 白名单 ====================
        // ⭐ 这是最不容易漏防的写法：**先假设全都要登录，再一条条开白名单**。
        //    反面教材（Day33 交付版）：addPathPatterns("/sku") 只精确匹配一条，
        //    结果 /sku/1、/sku/outbound 全都不设防 —— 同一组接口"一个拦一个不拦"。
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(WHITE_LIST);

        // ==================== ③ 实验用：preHandle 里抛异常 ====================
        registry.addInterceptor(new ThrowInterceptor())
                .addPathPatterns("/trace/throw/**");
    }
}