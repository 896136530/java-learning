package com.xiaoming.day35.web.trace;

import com.xiaoming.day35.auth.CurrentUser;
import com.xiaoming.day35.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ⭐ 观察窗口（调试接口）—— 今天所有"顺序"问题都靠它看
 *
 * 【本类索引：3 个接口】
 *   ┌──────────────────────┬────────────────────────────────────────────────────┐
 *   │ GET /trace/last      │ 本次请求(进行中)的轨迹 + 上一次已完成请求的完整轨迹   │
 *   │ GET /trace/whos      │ 当前线程的 ThreadLocal 里**还有谁**（看"没清理"的残留）│
 *   │ GET /trace/throw     │ 故意让拦截器在 preHandle 里抛异常（实验用）           │
 *   └──────────────────────┴────────────────────────────────────────────────────┘
 *
 * 【⭐ 怎么用才对（关键）】
 *   你想看"某次请求的完整轨迹"，不能在那次请求里看 —— 因为 **afterCompletion 是
 *   把响应写完才执行的**，那一刻你的 Controller 早就返回了。
 *   所以正确用法是**两步**：
 *     ① 先打一次要被观察的接口，比如 `GET /public/ping`
 *     ② 再打 `GET /trace/last` → 里面的 `lastCompleted` 就是①的完整轨迹（含 afterCompletion）
 *   ⭐ 这个"先打一次、再看上一次"的怪姿势，正好说明了一件重要的事：
 *     **拦截器的 afterCompletion 在响应之后 —— 它改不了响应，只能做事后收尾（清理/统计/写日志）**。
 *
 * 📌 这几个接口在 WebMvcConfig 里被**排除**在登录校验之外（白名单），
 *   否则没登录就看不了轨迹，调试起来很难受。
 */
@RestController
@RequestMapping("/trace")
public class TraceController {

    private final TraceRecorder recorder;

    public TraceController(TraceRecorder recorder) {
        this.recorder = recorder;
    }

    /**
     * GET /trace/last
     * 返回两段：
     *   currentRequest —— **本次请求**的轨迹（此时只有 Filter 和 preHandle，因为请求还没结束）
     *   lastCompleted  —— **上一次已完成**请求的完整轨迹（含 postHandle / afterCompletion）
     */
    @GetMapping("/last")
    public Result<Map<String, Object>> last() {
        Map<String, Object> data = new LinkedHashMap<>();

        Map<String, Object> currentRequest = new LinkedHashMap<>();
        currentRequest.put("uri", "/trace/last");
        currentRequest.put("thread", Thread.currentThread().getName());
        currentRequest.put("steps", recorder.steps());     // ⭐ 注意：只有前几步
        data.put("currentRequest", currentRequest);

        data.put("lastCompleted", recorder.lastCompleted());  // ⭐ 上一次的完整轨迹
        return Result.ok(data);
    }

    /**
     * GET /trace/whos
     * 看当前线程的 ThreadLocal 里"还有谁"。
     * ⭐ 正常情况：这里是 null（因为每次请求结束都清干净了）。
     *    如果 `/me` 之后打这个接口还能看到用户名 → **那次请求没清理 ThreadLocal**（越权隐患）。
     */
    @GetMapping("/whos")
    public Result<Map<String, Object>> whos() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("thread", Thread.currentThread().getName());
        data.put("currentUser", CurrentUser.get());     // null = 干净的
        return Result.ok(data);
    }

    /**
     * GET /trace/throw
     * 本接口什么都不干，但 ThrowInterceptor 会挂在这个路径上，在 preHandle 里抛异常。
     * 🎯 实验目的：**拦截器里抛异常，走不走统一异常处理器？**
     *    真机实测结论见 Day33 的 `练习题和代码题目.md` 第 1 步的观察点④。
     */
    @GetMapping("/throw")
    public Result<List<String>> throwIt() {
        // ⚠️ 这行**永远执行不到** —— 因为 preHandle 里已经抛异常了
        return Result.ok(List.of("这行永远不会执行"));
    }
}