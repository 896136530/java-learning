package com.xiaoming.day34.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ⭐ token 仓库（内存版）—— 登录发令牌、拦截器验令牌
 *
 * 【本类索引：3 个方法 + 2 张内存表】
 *   ┌──────────────────────────────┬───────────────────────────────────────────────┐
 *   │ login(user, pwd)             │ 账号密码对 → 发 token 并登记；不对 → 返回 null   │
 *   │ findUsername(token)          │ ⭐ 拦截器用的：拿 token 换用户名（换不到 = token 无效）│
 *   │ allTokens()                  │ 调试用：看看现在有哪些 token 没失效              │
 *   └──────────────────────────────┴───────────────────────────────────────────────┘
 *
 * 【真实项目里这里会是什么样？】
 *   内存 Map → **Redis**（Day44 学 JWT / Day45 学 Sa-Token 时你会看到）
 *   密码明文 → **BCrypt 加盐哈希**（绝不能存明文，这是安全事故）
 *   token 用 "tk-用户名" 这种可预测的字符串 → **UUID / JWT 签名串**（防伪造）
 *   📌 今天故意用最简单的做法（可预测 + 内存），因为今天要学的是**拦截器怎么用 token**，
 *      不是"token 怎么造"。**隔离无关变量** —— Day31/32 不用数据库也是这个道理。
 *
 * ⭐ 为什么它要是一个 Spring Bean（@Component）？
 *   因为**拦截器**要用它。而拦截器怎么拿到它，正是 Day33 最大的一个坑（Day33 题目第 5 步）。
 */
@Component
public class TokenStore {

    /** token → 用户名（ConcurrentHashMap：多线程安全，Day19 学过） */
    private final Map<String, String> tokenToUser = new ConcurrentHashMap<>();

    /** 用户名 → 密码（真实项目存的是哈希值，这里只为演示） */
    private final Map<String, String> userToPassword = new ConcurrentHashMap<>();

    public TokenStore() {
        // 两个演示账号（密码都是 123456）
        userToPassword.put("xiaoming", "123456");
        userToPassword.put("teacher", "123456");
    }

    /**
     * 登录：账号密码对就发一个 token 并登记，不对返回 null。
     * ⭐ token 规则故意做成"可预测"的（tk-用户名）—— 方便你在 PowerShell 里手打测试，
     *    也让文档里的"期望值"是确定的（每次跑都一样，不用记随机串）。
     */
    public String login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        if (!password.equals(userToPassword.get(username))) {
            return null;
        }
        String token = "tk-" + username;
        tokenToUser.put(token, username);
        return token;
    }

    /**
     * ⭐ 拦截器用这个方法：把请求头里的 token 换成用户名。
     * 返回 null = 这个 token 无效（没登录 / 过期 / 编造的）。
     */
    public String findUsername(String token) {
        if (token == null) {
            return null;
        }
        return tokenToUser.get(token);
    }

    /** 调试用：当前还有效的 token */
    public Set<String> allTokens() {
        return tokenToUser.keySet();
    }
}