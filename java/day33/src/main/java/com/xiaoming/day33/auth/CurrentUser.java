package com.xiaoming.day33.auth;

/**
 * ⭐⭐ "当前登录用户"的存放处 —— **今天必须理解的一个类**（虽然只有 3 行有效代码）
 *
 * 【本类索引：3 个静态方法】
 *   ┌──────────────────┬────────────────────────────────────────────────────┐
 *   │ set(username)    │ 拦截器验证通过后，把用户名放进当前线程              │
 *   │ get()            │ Service / Controller 里随便哪儿都能取出来           │
 *   │ clear()          │ ⭐ 请求结束时清掉（**必须在 afterCompletion 里调**）  │
 *   └──────────────────┴────────────────────────────────────────────────────┘
 *
 * 【它解决什么问题？】
 *   真实项目里，Controller / Service 里到处都要知道"现在是谁在操作"：
 *     下单要记"谁下的"、删数据要判"是不是他本人的"、日志要写"操作人"。
 *   笨办法是**一层层往下传参**：`orderService.create(OrderForm form, String username)`
 *   → 每个方法签名都要多一个参数，传三层就烦死了。
 *   ⭐ 聪明的办法就是它：**拦截器存一次，下面谁要用谁自己取**。
 *
 * 【为什么是 static + ThreadLocal？】
 *   · 拦截器是**单例**，Service 也是单例 → 不能把用户名存成"字段"（并发串数据）
 *   · ThreadLocal = **每个线程自己的储物柜** → 一个请求一个线程 → 天然隔离
 *   · static = 方便（不用到处注入这个类）；代价是"隐式依赖"（下面详解）
 *
 *   ⚠️ 这就是程序员吵了二十年的经典话题：
 *      支持派：省掉层层传参，方便（ThreadLocal 已被 Sa-Token / Spring Security 广泛使用）
 *      反对派：**调用方看不出这个方法需要登录**（隐式依赖），而且**忘了 clear 就是事故**
 *   📌 结论：能用，但必须守住两条纪律 ——
 *      **① 一定在 afterCompletion 里 clear()；② 单元测试里要自己 set（因为没经过拦截器）**。
 *
 * 【⚠️⚠️ 忘了 clear() 会怎样？（本题第 7 步的真机实测）】
 *   Tomcat 线程复用：请求 A（xiaoming）跑完，线程回到池子；
 *   下一个请求 B 恰好又分到这个线程 → B 的 `get()` 里**还是 xiaoming** →
 *   **B 没登录却拿到了 xiaoming 的身份** → 越权。
 *   而且它"时好时坏"（取决于分到哪个线程）—— 这类 bug 最难查。
 */
public class CurrentUser {

    /** ⭐ ThreadLocal<String>：只装一个用户名，每个线程一份 */
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private CurrentUser() {
        // 工具类不允许被 new（全静态）
    }

    /** 登录校验通过后调用（AuthInterceptor.preHandle 里） */
    public static void set(String username) {
        HOLDER.set(username);
    }

    /** 取当前登录用户；没登录 / 已清理时是 null */
    public static String get() {
        return HOLDER.get();
    }

    /** ⭐ 清掉（AuthInterceptor.afterCompletion 里调）—— 不调就是上面那个越权事故 */
    public static void clear() {
        HOLDER.remove();
    }
}