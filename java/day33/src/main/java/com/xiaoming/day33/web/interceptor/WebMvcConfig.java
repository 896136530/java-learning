package com.xiaoming.day33.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ⭐⭐ 拦截器注册处 —— 今天"谁拦谁不拦、谁先谁后"全在这个文件
 *
 * 【本类索引：3 条注册 + 2 个 TODO】
 *   ┌──────┬──────────────────────┬────────────────────────┬──────────────────────────┐
 *   │ 顺序 │ 拦截器                │ 拦截路径                │ 排除（白名单）            │
 *   ├──────┼──────────────────────┼────────────────────────┼──────────────────────────┤
 *   │ ①    │ TraceInterceptor     │ `/**`（全部）           │ 无（要观察所有请求）      │
 *   │ ②    │ AuthInterceptor      │ ⬜ TODO④ 要改           │ ⬜ TODO④ 要补             │
 *   │ ③    │ ThrowInterceptor     │ `/trace/throw/**`      │ 无（实验用）              │
 *   └──────┴──────────────────────┴────────────────────────┴──────────────────────────┘
 *
 * 【⭐ WebMvcConfigurer 是什么？】
 *   Spring MVC 留给你"改默认配置"的接口（有一堆 default 方法，用到哪个重写哪个）。
 *   今天只用它的一件事：`addInterceptors`。
 *   以后你还会用到：`addCorsMappings`（跨域）、`addResourceHandlers`（静态资源映射）、
 *   `addArgumentResolvers`（自定义参数解析）—— 同一个接口上的兄弟方法。
 *
 * 【⭐⭐ 注册顺序 = 执行顺序（今天的分号）】
 *   多个拦截器时：
 *     `preHandle`  **按注册顺序正序**执行（①→②→③）
 *     `postHandle` / `afterCompletion` **倒序**执行（③→②→①）
 *   为什么？—— 因为它们也是"包"结构：先进后出。
 *   📌 生活类比：进门要过两道安检（先刷脸、再查包），出门时顺序就反过来了（先拿包、再出门）。
 *
 * 【⚠️ 交付版这里有两处问题，就是题目第 5、6 步】
 *   问题 A（TODO③）：`new AuthInterceptor()` —— 不是 Spring Bean，导致它的 @Autowired 字段是 null。
 *   问题 B（TODO④）：`addPathPatterns("/sku")` —— **只精确匹配 /sku 这一条**，
 *                     `/sku/1`、`/sku/search` 这些**全都不设防**（越权！）。
 *   ⭐ 两处都属于"编译通过、启动正常、接口能跑"的类型 —— 只有真机打请求才看得出来。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * ⭐ 正确的范例：TraceInterceptor 是 @Component（Spring Bean），
     *    所以这里能用**构造器注入**拿到它，再注册。
     *    ⚠️ 对比下面那个 `new AuthInterceptor()` —— 一眼就能看出差别。
     */
    private final TraceInterceptor traceInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(TraceInterceptor traceInterceptor, AuthInterceptor authInterceptor) {
        this.traceInterceptor = traceInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // ==================== ① 轨迹记录：拦所有请求 ====================
        // ⭐ 故意**不排除任何路径** —— 因为要观察"/abc 这种 404 走不走拦截器"、
        //    "静态资源 /favicon.ico 走不走拦截器"。真实项目里通常要把静态资源排除掉。
        registry.addInterceptor(traceInterceptor)
                .addPathPatterns("/**");

        // ==================== ② 登录校验 ====================
        // ⭐⭐ TODO③（第 5 步）+ TODO④（第 6 步）都在下面这两行上：
        //
        //   问题 A（TODO③）：`new AuthInterceptor()` 造出来的对象**不是 Spring Bean**
        //       → 它里面的 @Autowired TokenStore 永远是 null
        //       → 带 token 访问 /sku 会抛 NullPointerException → 500
        //       修法：给 AuthInterceptor 加 @Component + 构造器注入（见 answer），
        //             然后本类改成"构造器注入 AuthInterceptor"，这里直接用字段。
        //
        //   问题 B（TODO④）：`"/sku"` 只匹配**这一条**路径
        //       → /sku/1、/sku/search、/sku/outbound 全都不设防（无 token 也能访问 = 越权）
        //       修法：改成 `"/**"`（拦全部），再用 excludePathPatterns 把白名单排除：
        //         .excludePathPatterns("/login", "/public/**", "/trace/**")
        //       ⭐ 白名单里的路径要**回 Controller 上抄**：
        //          AuthController 的 /login、PublicController 的 /public/**、
        //          TraceController 的 /trace/**（调试接口，不登录也得能看）
        // ================================================================
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/sku/**","/me/**","/cart/**","/order/**")
                .excludePathPatterns("/login", "/public/**", "/trace/**");

        // ==================== ③ 实验用：preHandle 里抛异常 ====================
        // 本拦截器没有依赖，new 出来完全没问题
        // ⭐ 判断题：ThrowInterceptor 没有 @Component、也没有任何依赖 → 保持 new（不必做成 Spring Bean）
        registry.addInterceptor(new ThrowInterceptor())
                .addPathPatterns("/trace/throw/**");
    }
}