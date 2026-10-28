package com.xiaoming.day35.db;

import com.xiaoming.day35.common.Result;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ⭐⭐ 连接池取证窗口（Day35 新增）—— 把 HikariCP 的"池子"变成看得见的数字
 *
 * 【本类索引：2 个接口】
 *   ┌──────────────────┬──────────────────────────────────────────────────────────┐
 *   │ GET /db/pool     │ 池子现在的样子：总连接数 / 忙的 / 空闲的 / 在排队的 / 配置参数 │
 *   │ POST /db/hold    │ 故意"占住"一条连接 n 毫秒（把池子打满用的实验工具）          │
 *   └──────────────────┴──────────────────────────────────────────────────────────┘
 *
 * 【⭐⭐ 为什么需要它？—— 连接池是"看不见的"，必须想办法把它变可见】
 *   Day34 你已经知道"有个连接池"，但你可能从没**见过**它。今天要回答四个问题：
 *     ① 池子起来的时候，它到底建了几条连接？（`minimum-idle` 默认是多少？）
 *     ② 一个请求处理完，连接是"关掉了"还是"还回池子"了？
 *     ③ `maximum-pool-size` 到了上限之后，第 N+1 个请求会怎么样？
 *     ④ "池子耗尽"的报错原文长什么样？（面试常问，光背"会报错"没用，要看原文）
 *   → `GET /db/pool` 回答 ①②，`/db/hold` + 并发请求回答 ③④。
 *
 * 【⭐⭐ HikariCP 的 MBean 是怎么拿到的？（这里有一个挺巧的知识点）】
 *   HikariCP 会把每个池子注册成 JMX 的 MBean，名字固定是这个格式：
 *       com.zaxxer.hikari:type=Pool (HikariPool-1)
 *   然后用 `HikariPoolMXBean` 读它的实时数字（getTotalConnections / getActiveConnections …）。
 *   ⭐ 这是"连接池监控"的标准做法：真实项目里这些数字会被 Prometheus / 监控面板抓走，
 *     所以**"池子够不够用"从来不是靠猜，是看 idle / waiting 两个数**：
 *       · `waiting > 0` 持续出现 → 池子小了（或者有慢 SQL 占着连接不放）
 *       · `idle` 一直是 0      → 同上
 *       · 两个都正常但 `active` 常年很高 → 可能有"连接泄漏"（借了没还）
 *
 * 【⚠️ 一个容易混淆的点：`url` 里那个 H2 参数 `DB_CLOSE_DELAY=-1`】
 *   它跟连接池没关系，是 H2 自己的"别删内存库"开关。
 *   但它和连接池**会打架**：不加这个参数，HikariCP 空闲回收连接时把最后一条也关了，
 *   H2 就顺手把整个内存库删了 → 你会看到莫名其妙的"表不存在"。
 *   ⭐ 换到真 MySQL 之后这个坑自动消失（MySQL 的数据不在你的进程里）。
 */
@RestController
@RequestMapping("/db")
public class PoolController {

    private static final Logger log = LoggerFactory.getLogger(PoolController.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbc;

    public PoolController(DataSource dataSource, JdbcTemplate jdbc) {
        this.dataSource = dataSource;
        this.jdbc = jdbc;
    }

    /**
     * 应用启动时先确认一句："我拿到的到底是不是 HikariCP？"
     *
     * ⭐ 为什么要打这行日志？
     *   因为 `DataSource` 是个**接口** —— 换成 Druid / DBCP / Tomcat JDBC 都实现它，
     *   代码一行不用改。SpringBoot 默认给的是 HikariCP，但**"默认"这件事值得用日志确认一次**。
     *   （Day35 的知识点里说的"SpringBoot 默认 HikariCP"就是靠这个看出来的。）
     */
    @PostConstruct
    public void sayHello() {
        log.info("[pool] DataSource 的真实类型 = {}", dataSource.getClass().getName());
    }

    /** 池子的实时快照 + 关键配置 */
    @GetMapping("/pool")
    public Result<Map<String, Object>> pool() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("dataSourceType", dataSource.getClass().getName());
        if (dataSource instanceof HikariDataSource hikari) {
            m.put("poolName", hikari.getPoolName());
            m.put("jdbcUrl", hikari.getJdbcUrl());
            m.put("driverClassName", hikari.getDriverClassName());
            // ── 关键参数（第 9 步要你把这些数字抄进表格）──
            m.put("maximumPoolSize", hikari.getMaximumPoolSize());
            m.put("minimumIdle", hikari.getMinimumIdle());
            m.put("connectionTimeoutMs", hikari.getConnectionTimeout());
            m.put("idleTimeoutMs", hikari.getIdleTimeout());
            m.put("maxLifetimeMs", hikari.getMaxLifetime());
            // ── 实时状态（HikariPoolMXBean 给的）──
            try {
                var mx = hikari.getHikariPoolMXBean();
                if (mx == null) {
                    m.put("mxbean", "还没拿到（池子可能还没启动）");
                } else {
                    m.put("totalConnections", mx.getTotalConnections());     // 池子里一共有几条
                    m.put("activeConnections", mx.getActiveConnections());   // 现在被借出去几条
                    m.put("idleConnections", mx.getIdleConnections());       // 现在空闲几条
                    Map<String, Object> threads = new LinkedHashMap<>();
                    threads.put("waitingForConnection", mx.getThreadsAwaitingConnection()); // ⭐ 在排队等连接
                    m.put("threads", threads);
                }
            } catch (Exception e) {
                m.put("mxbean", "读取失败：" + e.getMessage());
            }
        } else {
            m.put("note", "不是 HikariCP（本项目默认就是 Hikari）；其余字段跳过");
        }
        // 顺手报一个"这条查询用的是哪条连接"——如果池子有 10 条，你会看到不同的 id
        m.put("currentConnectionId", currentConnectionId());
        return Result.ok(m);
    }

    /**
     * ⭐⭐ Day35 真机实测撞出来的**又一个方言差异**：查"当前连接 id"的函数两边不一样
     *   ┌──────────┬──────────────────────────┐
     *   │ MySQL    │ `SELECT CONNECTION_ID()`  │
     *   │ H2 2.2.2 │ `SELECT SESSION_ID()`     │
     *   └──────────┴──────────────────────────┘
     *   实测原文（H2 上写 CONNECTION_ID()）：
     *     org.springframework.jdbc.BadSqlGrammarException:
     *       StatementCallback; bad SQL grammar [SELECT CONNECTION_ID()]
     *     ""CONNECTION_ID" is not a recognized built-in function name"
     *   ⭐ 这个数字是"证明连接池真的在复用连接"的关键证据（第 9 步）：
     *      · 同一个接口连打 10 次 → MySQL 上大概率看到**同一个 id**（连接没换）
     *      · 把池子调小再并发 → 看到 id 在几个值之间轮转
     *   所以这里做一次 try/catch 兜底：**先试 MySQL 的写法，失败了用 H2 的**。
     *   📌 注意这只影响"我自己的调试接口"，业务代码不依赖任何一边的函数
     *      —— 这就是"方言差异只该出现在边界上"的一条实操纪律。
     */
    private Long currentConnectionId() {
        try {
            return jdbc.queryForObject("SELECT CONNECTION_ID()", Long.class);
        } catch (Exception e) {
            return jdbc.queryForObject("SELECT SESSION_ID()", Long.class);
        }
    }

    /**
     * ⭐⭐ 故意占住一条连接 n 毫秒 —— **把连接池打满的实验工具**
     *
     * 【为什么要 `@Transactional`？—— 这是本方法的关键】
     *   一个方法只有在"事务里"才会**从头到尾握着同一条连接**。
     *   不加 `@Transactional` 的话，JdbcTemplate 每次执行 SQL 都是
     *   "借一条 → 用完立刻还" —— 你 sleep 的时候连接早就还回去了，池子根本不会满。
     *   ⭐ 一句话：**事务的边界 = 连接的持有边界。**（Day34 学的"事务"，今天从连接池的角度再看一遍）
     *
     * 【怎么用它把池子打满？（第 9 步会做）】
     *   ① 把 `application.properties` 里加三行（**改完必须重启**）：
     *        spring.datasource.hikari.maximum-pool-size=2
     *        spring.datasource.hikari.minimum-idle=2
     *        spring.datasource.hikari.connection-timeout=2000
     *   ② 同时打 4 个 `/db/hold?ms=5000`（**并发**，不是一个个打）
     *   ③ 观察：前 2 个 200，后 2 个变成 500，日志里有
     *        `Connection is not available, request timed out after 2xxx ms`
     *   ④ 看完**把那三行删掉/注释掉**，恢复默认（不然平时的实验会莫名其妙超时）
     *
     * ⚠️ 注意这里故意先跑一句真的 SQL（`SELECT 1`）：只 sleep 不碰数据库的话，
     *    某些情况下 Spring 的懒连接策略会"根本没去借连接"，实验就失败了。
     */
    @Transactional
    @GetMapping("/hold")
    public Result<Map<String, Object>> hold(@RequestParam(value = "ms", defaultValue = "3000") long ms)
            throws InterruptedException {
        Long connId = currentConnectionId();
        log.info("[pool] /db/hold 借到连接 CONNECTION_ID={}，准备占住 {} ms", connId, ms);
        Thread.sleep(ms);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("heldMs", ms);
        m.put("connectionId", connId);
        m.put("message", "这条连接被我占住了 " + ms + " ms（事务没结束，连接不会还回池子）");
        return Result.ok(m);
    }
}