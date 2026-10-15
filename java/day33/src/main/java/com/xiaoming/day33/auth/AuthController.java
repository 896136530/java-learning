package com.xiaoming.day33.auth;

import com.xiaoming.day33.common.Result;
import com.xiaoming.day33.common.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 登录相关接口（本项目只有两个）
 *
 * 【本类索引：2 个接口】
 *   ┌────────────────────┬────────────────────────────────────────────────────────┐
 *   │ POST /login        │ 传 {"username","password"} → 200 + token（错的 → 401）  │
 *   │ GET  /me           │ 需要 token；返回"我是谁"（从 CurrentUser 里取）           │
 *   └────────────────────┴────────────────────────────────────────────────────────┘
 *
 * 【⭐ 为什么 /login 必须放进拦截器的"白名单"？】
 *   因为**登录本身就是"还没有 token"的时候干的事**。
 *   如果拦截器把 /login 也拦下（要求带 token），就会变成一个死循环：
 *     想拿 token → 得先登录 → 登录要求带 token → 想拿 token …
 *   📌 这就是知识点第 8 节那个坑："白名单漏了 /login，登录接口自己被拦"。
 *      今天题目第 1 步就能亲眼看到它（或者看到它被正确放行）。
 *
 * 【⭐ /me 是干什么的？—— 它是"ThreadLocal 到底生效没有"的窗口】
 *   它自己**从来没接收过用户名参数**，却能报出是谁 ——
 *   因为 AuthInterceptor 在 preHandle 里把用户名放进了 ThreadLocal。
 */
@RestController
public class AuthController {

    private final TokenStore tokenStore;

    public AuthController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /**
     * POST /login
     * 请求体：{"username":"xiaoming","password":"123456"}
     * 成功 → 200 {"code":"200","data":{"username":"xiaoming","token":"tk-xiaoming"}}
     * 失败 → 401 {"code":"401","message":"用户名或密码错误"}
     *
     * 📌 这里图省事直接用 Map 接收（真实项目应该建一个 LoginForm DTO + @NotBlank 校验，
     *    就像 Day32 的 SkuForm 那样；今天的主角不是校验，就不加戏了）。
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        String token = tokenStore.login(username, password);
        if (token == null) {
            // ⭐ 401 而不是 400：不是"参数格式错"，是"身份不对"
            throw new UnauthorizedException("用户名或密码错误");
        }

        Map<String, String> data = new LinkedHashMap<>();
        data.put("username", username);
        data.put("token", token);
        return Result.ok(data);
    }

    /**
     * GET /me  （请求头要带 X-Token）
     * 返回当前登录用户 + 处理这个请求的 Tomcat 线程名。
     * ⭐ 线程名要特意返回出来：第 7 步观察 ThreadLocal 残留时，要靠它对上号。
     */
    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", CurrentUser.get());          // ⭐ 从 ThreadLocal 里取，谁都没传参给我
        data.put("thread", Thread.currentThread().getName());
        return Result.ok(data);
    }
}