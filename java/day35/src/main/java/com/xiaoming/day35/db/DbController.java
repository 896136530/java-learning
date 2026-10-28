package com.xiaoming.day35.db;

import com.xiaoming.day35.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ⭐⭐ 查库窗口（今天的"验尸报告"接口）—— 全是 SELECT，不写一条业务逻辑
 *
 * 【本类索引：4 个查询 + 1 个复位 + 2 个写接口 + 1 个字符集实验】
 *   ┌───────────────────────┬──────────────────────────────────────────────────────┐
 *   │ GET /db/state         │ ⭐ 一眼看全：orders 行数 / audit_log 行数 / sku 三行库存│
 *   │ GET /db/orders        │ 订单表全部内容（按 id）                                │
 *   │ GET /db/audit         │ 审计表全部内容（按 id）                                │
 *   │ GET /db/sku           │ 商品表全部内容（按 id）                                │
 *   │ POST /db/reset        │ ⭐ 复位：清空 + 重插种子（每次做实验前先点它！）          │
 *   │ GET /db/sql?q=        │ ⭐ 自由**查询**：只接受 SELECT（底层是 queryForList）   │
 *   │ GET /db/exec?q=       │ ⭐⭐ Day35 新增：自由**执行**（INSERT/UPDATE/DELETE）  │
 *   │ GET /db/charset-demo  │ ⭐⭐ Day35 新增：utf8 vs utf8mb4 的 emoji 对照实验      │
 *   └───────────────────────┴──────────────────────────────────────────────────────┘
 *
 * ⭐ Day35 新增的"表结构取证"在**另一个类**里（本包 SchemaController）：
 *     /db/columns、/db/indexes、/db/keys —— 类型 / 约束 / 索引 / 字符集都在那儿看。
 *     为什么分开放？因为这两个类回答的是**两个不同的问题**：
 *       DbController      → "数据现在是什么样"（行数、内容 → 事务实验用）
 *       SchemaController  → "表结构是什么样"（类型、约束 → Day35 验收用）
 *     ⭐ 还有第三个新类 PoolController → "连接池现在是什么样"（/db/pool、/db/hold）
 *
 * ⚠️ Day35 有一个数字变了（别照抄 Day34 的期望值）：
 *     `auditLog` 启动后是 **1**（不是 0）—— 因为 DataInitializer 现在会插一条
 *     `('DAILY_CLOSE', '日终对账完成 <今天>')` 当"联合唯一索引"的靶子。
 *     `/db/reset` 之后也**还是 1**（不是 0）。在 /db/audit 里能看到它。
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

    private static final Logger log = LoggerFactory.getLogger(DbController.class);

    private final JdbcTemplate jdbc;
    private final DataInitializer initializer;
    private final DataSource dataSource;

    public DbController(JdbcTemplate jdbc, DataInitializer initializer, DataSource dataSource) {
        this.jdbc = jdbc;
        this.initializer = initializer;
        this.dataSource = dataSource;
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
        return Result.ok(jdbc.queryForList("SELECT id, code, title, price, stock, is_deleted FROM sku ORDER BY id"));
    }

    /**
     * ⭐ 复位：把数据恢复到"刚启动"的样子。
     * 规矩：**复位 → 只做一个操作 → 立刻查库**（不然实验结论会串味）
     */
    @PostMapping("/reset")
    public Result<String> reset() {
        initializer.reset();
        return Result.ok("已复位：orders=0, audit_log=1（那条 DAILY_CLOSE 靶子）, sku=3 条种子");
    }

    /**
     * ⭐ 自由查询（本地调试用，别学到生产里去）
     * 例：/db/sql?q=SELECT COUNT(*) AS N FROM orders
     */
    @GetMapping("/sql")
    public Result<List<Map<String, Object>>> sql(@RequestParam("q") String q) {
        return Result.ok(jdbc.queryForList(q));
    }

    /**
     * ⭐⭐ Day35 新增：**执行**一句写操作（INSERT / UPDATE / DELETE）
     * 例：/db/exec?q=INSERT INTO sku (code,title,price,stock) VALUES ('SKU-0001','重复',1.00,1)
     *
     * 【为什么必须另开一个接口，不能继续用 /db/sql？】
     *   真机实测（H2）：
     *     `GET /db/sql?q=INSERT ...`
     *     → 500，日志里是 `Method is only allowed for a query. Use execute or executeUpdate
     *       instead of executeQuery`（H2 error code 90002）
     *   原因：`/db/sql` 底下调的是 `JdbcTemplate.queryForList(...)` —— **它只接受 SELECT**。
     *   📌 所以 `/db/sql` **不能**用来插数据。想插数据、想亲手撞约束，只能走这个 `/db/exec`。
     *
     * 【今天为什么要它？—— 它是"债点"实验的起爆器】
     *   第 7 步要你**绕过 Service 层**，直接让数据库去拒绝一次重复插入：
     *     ① 用 /db/exec 插一个已经存在的 `code` → **唯一索引拒绝** → 交付版 **500**
     *     ② 用 /db/exec 插一条 `sku_id = 999` 的订单 → **外键拒绝** → 500
     *     ③ 用 /db/exec 插 `stock = 501` → **CHECK 拒绝** → 500
     *   这三条都是"应用层没想到、数据库兜住了"的活证据（Day32 那张表的第 ③ 层）。
     *
     * 【它返回什么？】
     *   成功 → `{"code":"200", ..., "data":1}`（data 是**影响行数**）
     *      ⭐ 注意是一层 List：INSERT 的影响行数也放在 `data:[{...}]` 里对外统一（本项目所有接口
     *        的 data 形状保持一致，前端就不用为这一个接口写特例）。
     *   失败 → 交付版走兜底 handler → **500**（想看数据库的原话，去启动窗口里翻日志）
     *
     * 【⚠️⚠️ 安全警告（必读，因为这是"故意留下的后门"）】
     *   这个接口**能执行任意 SQL** —— 等于把数据库的控制权交出去。
     *   本项目敢开着它，因为：① 本地库、root/root、跑完就关；② 它是今天实验的必需品。
     *   ⭐ 但你必须记住：**真实项目里绝对不能有这种接口**（哪怕加了权限也不行，
     *     因为一个 SQL 注入就能变成删库）。上线前必须删掉它 —— 这是"方便"和"危险"
     *     是同一个东西的两面（Day34 的 /db/sql 注释里已经说过一次，今天多了一个接口，再说一次）。
     */
    @GetMapping("/exec")
    public Result<List<Map<String, Object>>> exec(@RequestParam("q") String q) {
        int rows = jdbc.update(q);
        log.warn("[db/exec] 执行了写操作，影响 {} 行：{}", rows, q);
        Map<String, Object> one = new LinkedHashMap<>();
        one.put("affectedRows", rows);
        one.put("sql", q);
        return Result.ok(List.of(one));
    }

    /**
     * ⭐⭐ Day35 新增：**字符集对照实验** —— utf8 vs utf8mb4，同一个 emoji 两种命运
     *
     * 【为什么单独写一个接口，而不是让你敲 SQL？——为了让你看到"数据库的原话"】
     *   如果走 `/db/exec`，异常会被 JdbcTemplate 包装成
     *   `DataIntegrityViolationException`（Spring 的类），
     *   而 `e.getCause()` 里**有时候看不到最底层那句 MySQL 原文**。
     *   这个接口故意**绕过 JdbcTemplate**，直接用 `Connection.createStatement()` 执行，
     *   于是 `SQLException.getMessage()` 就是数据包里的原话（一点没加工）。
     *   ⭐ 这是"排查数据库问题"的基本功：**要看到驱动原话，就别隔着框架看。**
     *
     * 【预期结果（真机实测 MySQL 8.4.5）】
     *   utf8_col   ← emoji → 失败：`Incorrect string value: '\xF0\x9F\x98\x80' for column 'utf8_col' at row 1`（1366）
     *   mb4_col    ← emoji → 成功，并且 HEX 是 F09F9880（**4 字节**）
     *   ⚠️ 这张表**只在 MySQL 上存在**（H2 不支持列级 CHARACTER SET），
     *      所以交付版跑 H2 时本接口会返回 **200 + `chDemoExists=false` + 一句 `note`**
     *      —— 这是**预期**，不是 bug。
     *   ⭐ 真机复核时这里原本是 **500**（`Function "HEX" not found`）：守卫只写在
     *      `catch (SQLException)` 里，而 **H2 也认 `DATABASE()`**（返回 `DAY35`），
     *      所以"表不存在"这件事**根本不抛异常**，只是查出 0 行 → 代码继续往下跑 →
     *      最后死在 H2 2.x 没有的 `HEX()` 上。⭐ **守卫要看"查询结果"，不能只看"有没有抛异常"。**
     */
    @GetMapping("/charset-demo")
    public Result<Map<String, Object>> charsetDemo() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection(); Statement st = conn.createStatement()) {

            // 第一步：先确认这张表存在（H2 上它不存在 —— 这就是"方言差异"的证据之一）
            //   ⚠️⚠️ 真机复核踩过的坑：H2 **也认 `DATABASE()`**（返回 `DAY35`），
            //      所以"表不在"**不会抛 SQLException**，只会**查出 0 行**。
            //      老写法只在 catch 里 return → `chDemoExists=false` 却继续往下跑 →
            //      最后撞上 H2 2.x 没有的 `HEX()` → 接口 500（而不是"表不存在"）。
            //   ⭐ 所以守卫必须落在**查询结果**上：查出来是 0 行，就立刻带 note 返回。
            boolean chDemoExists;
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                            + "WHERE TABLE_NAME = 'CH_DEMO' AND TABLE_SCHEMA = DATABASE()")) {
                rs.next();
                chDemoExists = rs.getInt(1) > 0;
            } catch (SQLException e) {
                // 极端情况：连 INFORMATION_SCHEMA 都查不了（比如方言完全不认 DATABASE()）
                chDemoExists = false;
            }
            m.put("chDemoExists", chDemoExists);
            if (!chDemoExists) {
                m.put("note", "ch_demo 不存在（H2 建不出这张表；MySQL 上由 schema.sql 建）。"
                        + "本接口剩下的 emoji 对照实验已跳过 —— 这是**预期**结果，不是 bug。");
                return Result.ok(m);
            }

            // 第 0 步：先把 ch_demo 清空 —— **让每次调用都能重复做同一个实验**（幂等）
            try {
                st.executeUpdate("TRUNCATE TABLE ch_demo");
            } catch (SQLException ignored) {
                // 表不存在时（H2）上面那步已经 return 了；这里兜一下"没权限清"的极端情况
            }

            // 第二步：往 utf8 列插 emoji —— 失败是**必然**的，我们要的就是那句原话
            m.put("step1_utf8_col", tryInsertEmoji(st, "utf8_col", "mb4_col"));

            // 第三步：往 utf8mb4 列插同一个 emoji —— 成功，并且 HEX 是 4 字节
            m.put("step2_mb4_col", tryInsertEmoji(st, "mb4_col", "utf8_col"));

            // 第四步：把 mb4 列存进去的东西读出来，看它到底是几个字节
            try (ResultSet rs = st.executeQuery(
                    "SELECT mb4_col, HEX(mb4_col) AS hx, LENGTH(mb4_col) AS bytes, "
                            + "CHAR_LENGTH(mb4_col) AS chars FROM ch_demo ORDER BY id DESC LIMIT 1")) {
                if (rs.next()) {
                    Map<String, Object> read = new LinkedHashMap<>();
                    read.put("value", rs.getString("mb4_col"));
                    read.put("hex", rs.getString("hx"));
                    read.put("lengthBytes", rs.getInt("bytes"));   // 4（emoji 真的占了 4 个字节）
                    read.put("charLength", rs.getInt("chars"));    // 1（但它只是 1 个字符）
                    m.put("step3_readBack", read);
                }
            }
            m.put("conclusion", "utf8 是「假 UTF-8」（最多 3 字节），emoji 要 4 字节 → 只能 utf8mb4。"
                    + "建库建表一律 utf8mb4；JDBC 的 characterEncoding 参数却要写 UTF-8（两套命名）。");
        }
        return Result.ok(m);
    }

    /** 试着往某一列插 emoji：成功返回 ok，失败返回**数据库原话** */
    private Map<String, Object> tryInsertEmoji(Statement st, String column, String otherColumn) {
        Map<String, Object> r = new LinkedHashMap<>();
        String emoji = "😀";
        // ⚠️⚠️ 这里有个**必须处理**的细节（真机踩过）：ch_demo 两列都是 NOT NULL，
        //   所以 INSERT 时**两列都要给值**，否则会撞上另一列的 NOT NULL：
        //     ERROR 1364 (HY000): Field 'utf8_col' doesn't have a default value
        //   ⭐ 更坑的是：那个错误会**把真正想看的 1366 盖掉**（因为它在 1366 之前就报了）。
        //   所以：要测哪一列，另一列就填一个**普通 ASCII 字符 'x'**（保证它一定塞得进去）。
        String sql = "INSERT INTO ch_demo (" + column + ", " + otherColumn + ") VALUES ('" + emoji + "', 'x')";
        try {
            st.executeUpdate(sql);
            r.put("sql", sql);
            r.put("result", "成功");
            return r;
        } catch (SQLException e) {
            r.put("sql", sql);
            r.put("result", "失败");
            r.put("errorCode", e.getErrorCode());
            r.put("sqlState", e.getSQLState());
            r.put("message", e.getMessage());   // ⭐ 这就是数据库的原话，没被框架包装过
            return r;
        }
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