package com.xiaoming.day34.publicapi;

import com.xiaoming.day34.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 公开接口 —— 专门用来演示"拦截器白名单"的
 *
 * 【本类索引：2 个接口】
 *   ┌─────────────────────┬────────────────────────────────────────────────┐
 *   │ GET /public/ping    │ 探活：{"pong":true,...}                        │
 *   │ GET /public/notice  │ 公告：返回一条静态消息                          │
 *   └─────────────────────┴────────────────────────────────────────────────┘
 *
 * 【⭐ 为什么它们不需要登录？】
 *   首页、公告、健康检查这些接口，本来就不该要求用户先登录。
 *   做法不是"在 Controller 里 if 一下"，而是在拦截器注册时把它们排除掉：
 *     `.excludePathPatterns("/public/**")`
 *
 *   ⭐ 生活类比：小区大门要刷脸，但门口的信报箱、垃圾分类点不该要刷脸 ——
 *      不是"具体每个地方都写一句"，而是**大门保安手里有一张"免检场所清单"**。
 *
 * 📌 注意路径写法：`/public/**` 里的 `**` 表示"这个目录下的所有路径"，
 *    能一次匹配 /public/ping 和 /public/notice。
 *    今天题目第 6 步就要你练这个通配符（写窄了就漏挡一片，写错字了就等于没写）。
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    /** GET /public/ping —— 探活（不需要登录） */
    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pong", true);
        data.put("time", LocalDateTime.now().toString());
        return Result.ok(data);
    }

    /** GET /public/notice —— 公告（不需要登录） */
    @GetMapping("/notice")
    public Result<String> notice() {
        return Result.ok("今日闭馆时间 22:00，请合理安排借阅");
    }
}