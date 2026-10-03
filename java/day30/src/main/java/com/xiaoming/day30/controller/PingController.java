package com.xiaoming.day30.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 最小可用接口（已写好）—— 用来"验证服务器活着"
 *
 * 为什么要这个？排查问题时**先确认最简单的东西能通**，再排查复杂的。
 * 如果 /ping 都打不开 → 说明是服务器没起来/端口不对；
 * 如果 /ping 能开但 /tasks 报 500 → 说明是数据库那段的问题。
 *
 * 这个排查思路（从最简单的开始，一层层往上）比"盯着报错猜"快 10 倍。
 *
 * 访问：http://localhost:8080/ping
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "ok");
        m.put("message", "SpringBoot 服务器活着 ✅");
        m.put("time", java.time.LocalDateTime.now().toString());
        return m;
    }
}