package com.xiaoming.day34.db;

import com.xiaoming.day34.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ⭐⭐ 查库窗口（今天的"验尸报告"接口）—— 全是 SELECT，不写一条业务逻辑
 *
 * 【本类索引：4 个查询 + 1 个复位 + 1 个自由 SQL】
 *   ┌──────────────┬─────────────────────────────────────────────────────────┐
 *   │ GET /db/state │ ⭐ 一眼看全：orders 行数 / audit_log 行数 / sku 三行库存   │
 *   │ GET /db/orders│ 订单表全部内容（按 id）                                   │
 *   │ GET /db/audit │ 审计表全部内容（按 id）                                   │
 *   │ GET /db/sku   │ 商品表全部内容（按 id）                                   │
 *   │ POST /db/reset│ ⭐ 复位：清空 + 重插种子（每次做实验前先点它！）             │
 *   │ GET /db/sql   │ ⭐ 自由查询：/db/sql?q=SELECT COUNT(*) AS N FROM orders    │
 *   └──────────────┴─────────────────────────────────────────────────────────┘
 *
 * 【⭐⭐ 为什么今天必须有一个"查库窗口"？】
 *   因为**事务回滚这件事，响应码和响应体根本看不出来**：
 *     · 成功下单：200 + 订单 JSON
 *     · 抛异常：500 + {"code":"500","message":"服务器开小差了，请稍后重试"}
 *     · 抛异常但**没回滚**：**一模一样**的 500 + 同一句话
 *   也就是说：**同一个 500，背后可能是"干净回滚"，也可能是"脏数据已经写进去了"。**
 *   → 唯一的硬证据就是回数据库里数行数。
 *
 *   ⭐ 生活类比：警察办案不看"你说了什么"（响应码），看"现场留下了什么"（数据库）。
 *      这就是为什么今天所有验收标准都写成"查库的行数 / 内容"，而不是"HTTP 200 还是 500"。
 *
 * 【/db/sql 是干什么的？会不会危险？】
 *   真实项目里**绝对不能**开放一个能执行任意 SQL 的接口（等于把数据库交出去）。
 *   这里开放是因为：① 本地内存库、跑完就没了；② 你想验证什么就敲什么，不用改代码重打包。
 *   ⭐ 你上生产项目时，这类调试接口必须删掉或加权限——记住"方便"和"危险"是同一个东西的两面。
 */
@RestController
@RequestMapping("/db")
public class DbController {

    private final JdbcTemplate jdbc;
    private final DataInitializer initializer;

    public DbController(JdbcTemplate jdbc, DataInitializer initializer) {
        this.jdbc = jdbc;
        this.initializer = initializer;
    }

    /** ⭐ 最常用的一个：一眼看全三张表的"关键数字" */
    @GetMapping("/state")
    public Result<Map<String, Object>> state() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("orders", count("orders"));
        m.put("auditLog", count("audit_log"));
        m.put("sku", skuStock());
        return Result.ok(m);
    }

    /** 订单表全部内容 */
    @GetMapping("/orders")
    public Result<List<Map<String, Object>>> orders() {
        return Result.ok(jdbc.queryForList("SELECT id, sku_id, title, count, amount FROM orders ORDER BY id"));
    }

    /** 审计表全部内容 */
    @GetMapping("/audit")
    public Result<List<Map<String, Object>>> audit() {
        return Result.ok(jdbc.queryForList("SELECT id, action, detail FROM audit_log ORDER BY id"));
    }

    /** 商品表全部内容（重点看 stock 那一列） */
    @GetMapping("/sku")
    public Result<List<Map<String, Object>>> sku() {
        return Result.ok(jdbc.queryForList("SELECT id, code, title, price, stock FROM sku ORDER BY id"));
    }

    /**
     * ⭐ 复位：把数据恢复到"刚启动"的样子。
     * 规矩：**复位 → 只做一个操作 → 立刻查库**（不然实验结论会串味）
     */
    @PostMapping("/reset")
    public Result<String> reset() {
        initializer.reset();
        return Result.ok("已复位：orders=0, audit_log=0, sku=3 条种子");
    }

    /**
     * ⭐ 自由查询（本地调试用，别学到生产里去）
     * 例：/db/sql?q=SELECT COUNT(*) AS N FROM orders
     */
    @GetMapping("/sql")
    public Result<List<Map<String, Object>>> sql(@RequestParam("q") String q) {
        return Result.ok(jdbc.queryForList(q));
    }

    // ─────────────── 小工具 ───────────────

    /** 数某个表有多少行（表名是我们自己写死的，不来自外部输入，所以拼进 SQL 安全） */
    private Integer count(String table) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    }

    /** 三行 SKU 的库存快照，形如 {SKU-0001=10, SKU-0002=3, SKU-0003=0} */
    private List<Map<String, Object>> skuStock() {
        return jdbc.queryForList("SELECT code, stock FROM sku ORDER BY id");
    }
}