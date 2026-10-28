package com.xiaoming.day35.db;

import com.xiaoming.day35.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ⭐⭐ 表结构取证窗口 —— 「我到底建出了什么表？」的机器可读答案（Day35 新增）
 *
 * 【本类索引：3 个只读接口，全部只查 INFORMATION_SCHEMA（元数据），不碰业务表】
 *   ┌────────────────┬──────────────────────────────────────────────────────────────┐
 *   │ GET /db/columns│ 每一列：类型 / 允不允许 null / 默认值 / **字符集**             │
 *   │ GET /db/indexes│ 每一个索引：有哪几列、列的顺序（复合索引靠它看）                │
 *   │ GET /db/keys   │ 每一条约束：PRIMARY KEY / UNIQUE / FOREIGN KEY / CHECK        │
 *   └────────────────┴──────────────────────────────────────────────────────────────┘
 *
 * 【⭐⭐ 为什么 Day35 需要这个？（Day34 的 /db/state 不够用了）】
 *   Day34 只需要数行数（事务有没有回滚），所以 /db/state 就够了。
 *   今天要验收的东西**全在"表结构"里**：
 *     · price 到底是 INT 还是 DECIMAL(10,2)？   → /db/columns
 *     · code 上到底有没有唯一索引？             → /db/indexes + /db/keys
 *     · 那条**联合唯一索引**是哪两列、顺序如何？  → /db/indexes
 *     · 字符集是不是 utf8mb4？                  → /db/columns 的 CHARACTER_SET_NAME
 *   光靠"接口能跑通"是**判不出来**的 —— 字段类型错了接口照样 200。
 *   ⭐ 所以今天的验收口径是：**接口行为 + 表结构取证，两样都要看。**
 *
 * 【⭐ 为什么用 INFORMATION_SCHEMA 而不是 mysqldump / DESC？】
 *   INFORMATION_SCHEMA 是 SQL 标准里的"数据库的自我介绍"，
 *   **H2 和 MySQL 都有**，而且能用 SQL 查（意味着能被接口自动化）。
 *   这样同一个接口在 H2 和 MySQL 上都能用 —— 正好让你对比"同一份请求、两个数据库的答案"。
 *
 * 【⚠️ 两个数据库的元数据不是完全一样的（真机实测记录）】
 *   · H2 **没有** `INFORMATION_SCHEMA.TABLES.TABLE_COLLATION` 这一列
 *     → 查了会报 `Column "TABLE_COLLATION" not found`
 *   · H2 也**不认识** `@@sql_mode` 这种 MySQL 系统变量
 *   → 所以本类**故意只用两边都有的那几个字段**（COLUMNS / INDEX_COLUMNS / TABLE_CONSTRAINTS）。
 *     MySQL 专属的证据（`SHOW CREATE TABLE` / `@@sql_mode`）走 mysql 命令行，见题目第 1 步。
 *
 * 【⭐⭐ 还有一个"藏得很深"的方言差异，就是下面那个 schemaName()（真机实测踩出来的）】
 *   "当前是哪个库"这个函数，两边**名字不一样**：
 *     · H2      ：`CURRENT_SCHEMA()`
 *     · MySQL   ：`DATABASE()`
 *   ⚠️ 我先写的 `CURRENT_SCHEMA()`，在 MySQL 上报：
 *        ERROR 1305 (42000): FUNCTION day35.CURRENT_SCHEMA does not exist
 *      ⭐ 注意报错里那串 `FUNCTION day35.CURRENT_SCHEMA` —— MySQL 把它**当成"你自己库里的
 *         一个函数"**去找了（MySQL 不认这个名字），所以报的是"函数不存在"，很有迷惑性。
 *   → 处理办法：启动时**探测一次**，以后都用探测到的那个（见 schemaName()）。
 *
 * 【⭐⭐⭐ 还有第三个、也是最坑的一个差异：/db/indexes 在 MySQL 上直接 500】
 *   H2 的索引元数据在 `INFORMATION_SCHEMA.INDEX_COLUMNS`，
 *   而 **MySQL 里根本没有这张表** —— 真机实测：
 *     ERROR 1109 (42S02): Unknown table 'INDEX_COLUMNS' in information_schema
 *     → Spring 包成 BadSqlGrammarException → 接口 **500**
 *   MySQL 用的是 `INFORMATION_SCHEMA.STATISTICS`（列名也不一样：SEQ_IN_INDEX / NON_UNIQUE）。
 *   ⭐ 这套代码的写法是：**判断一次方言，分两条 SQL**（见 indexes()）。
 *     这才是"兼容两个库"的现实做法 —— 不是把 SQL 堆在一句里，而是**把差异关进一个地方**。
 *     📌 这就是"方言差异只该出现在一个地方"的实操：**接口层不要散落方言判断。**
 *
 * 【⚠️⚠️ 而"判断方言"这件事本身也踩了一个坑，值得单独记下来】
 *   我最初用"能不能跑通 `SELECT DATABASE()`"来判断是不是 MySQL，结论是**错的**：
 *   **H2 2.2.224 也认 `DATABASE()`**（返回 `DAY35`）。后果有两层，第二层更可怕：
 *     ① `/db/indexes` 走了 MySQL 的 STATISTICS 分支 → `bad SQL grammar` → **500**
 *     ② 更要命的是 `/db/columns` 和 `/db/keys`：它们变成
 *        `TABLE_SCHEMA = DATABASE()`，在 H2 上就是拿 `DAY35` 去比对元数据里真正存的 `PUBLIC`
 *        → **查出空列表**（HTTP 200，data 是 []）—— **静默出错，比 500 难查得多！**
 *   ⭐ 正解：看 JDBC 元数据的 `getDatabaseProductName()`（H2 → `"H2"`，MySQL → `"MySQL"`）。
 *   📌 一句话教训：**"能不能跑通"不是可靠的检测手段；要问权威来源。**
 */
@RestController
@RequestMapping("/db")
public class SchemaController {

    private final JdbcTemplate jdbc;

    /**
     * "当前库叫啥"的**方言适配**：MySQL 用 `DATABASE()`，H2 用 `CURRENT_SCHEMA()`
     *
     * ⭐ 为什么在字段初始化时探测一次、而不是每次查询都 try/catch？
     *   ① 每个请求都 try/catch 一次失败的 SQL，会**白打一次数据库**（还污染日志）
     *   ② "探测一次、结果复用"是标准做法（一启动就知道自己连的是谁）
     *   ③ 万一两个都不认（第三种数据库），`schemaName` 会是 DATABASE()，
     *      查询报错时你能立刻从 SQL 里看出原因 —— 比"静默返回空列表"好得多
     */
    private final String schemaName;

    /**
     * ⭐ 方言开关是怎么判断的？—— **别猜，去看元数据**（这里踩过一个很典型的坑）
     *
     * 【先说踩的坑：我一开始用 `SELECT DATABASE()` 当探针，结果完全判错了】
     *   思路是"H2 没有 DATABASE()，所以能跑通的就是 MySQL" —— 听起来很有道理，**但它是错的**：
     *   真机实测（H2 2.2.224）：`SELECT DATABASE()` **H2 也认**，返回 `DAY35`（就是当前 schema）。
     *   → 于是 H2 被误判成 MySQL → 走了 MySQL 的 `INFORMATION_SCHEMA.STATISTICS` 分支
     *     → 报 `bad SQL grammar` → `/db/indexes` **500**
     *     （同时 `TABLE_SCHEMA = DATABASE()` 在 H2 上会拿库名 `DAY35` 去比对，
     *      而 H2 的元数据里存的是 schema 名 `PUBLIC` → 查出来**空列表**，静默出错更难查！）
     *   ⭐ 教训：**"能不能跑通"不是可靠的检测手段** —— 不同数据库经常"恰好都认"某个名字。
     *
     * 【正确的做法：问它叫什么名字】
     *   JDBC 元数据里的 `getDatabaseProductName()` 是**权威答案**：
     *     · H2    → `"H2"`
     *     · MySQL → `"MySQL"`   （真机实测，见本包取证记录）
     *   → 一次判断，两个开关都定下来：`mysql`（用哪张元数据表）和 `schemaName`（用哪个函数）。
     *   📌 这也顺便回答了 H2 上"库名 vs schema 名"的疑惑：H2 里 `DATABASE()` 返回的是 **schema**
     *      （`DAY35` 在你看起来像库名，其实 H2 的"库"就是 schema 概念），
     *      而 H2 的元数据表里 `TABLE_SCHEMA` 列存的是 **`PUBLIC`** —— 所以两个写法**根本不是一回事**。
     */
    private final boolean mysql;

    public SchemaController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        String productName;
        try (java.sql.Connection conn = jdbc.getDataSource().getConnection()) {
            productName = conn.getMetaData().getDatabaseProductName();
        } catch (java.sql.SQLException e) {
            throw new IllegalStateException("读不到数据库元数据", e);
        }
        this.mysql = productName != null && productName.toLowerCase().contains("mysql");
        this.schemaName = mysql ? "DATABASE()" : "CURRENT_SCHEMA()";
        org.slf4j.LoggerFactory.getLogger(SchemaController.class)
                .info("[db] 元数据库方言 = {}（productName=\"{}\"；schemaName 用 {}；当前库 = {}）",
                        mysql ? "MySQL" : "H2/其他", productName, schemaName,
                        jdbc.queryForObject("SELECT " + schemaName, String.class));
    }

    /**
     * 每一列的"体检表"
     *
     * ⭐ 怎么读这张表（三个最常看的）：
     *   DATA_TYPE = 'DECIMAL' 且 NUMERIC_PRECISION=10 / NUMERIC_SCALE=2 → 金额类型是对的
     *   IS_NULLABLE = 'NO'                                             → 有 NOT NULL
     *   COLUMN_DEFAULT 不是 null                                       → 有 DEFAULT
     *   CHARACTER_SET_NAME = 'utf8mb4'                                 → 字符集是对的（MySQL 上才有值）
     *     ⚠️ H2 上这列是 'Unicode'，这是**两边不一样**的地方（诚实标注）
     */
    @GetMapping("/columns")
    public Result<List<Map<String, Object>>> columns() {
        return Result.ok(jdbc.queryForList("""
                SELECT TABLE_NAME, ORDINAL_POSITION, COLUMN_NAME, DATA_TYPE, IS_NULLABLE,
                       COLUMN_DEFAULT, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE,
                       CHARACTER_SET_NAME
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = """ + schemaName + """
                ORDER BY TABLE_NAME, ORDINAL_POSITION
                """));
    }

    /**
     * 每一个索引的"花名册"
     *
     * ⭐ 复合索引怎么看出来？—— 同一个 INDEX_NAME **连着出现两行**，
     *   ORDINAL_POSITION = 1 是第一列、2 是第二列。
     *   例：uk_sku_code_alive → (1) CODE, (2) IS_DELETED
     *
     * ⚠️ 这里**看不出来"唯不唯一"** —— INDEX_COLUMNS 在 H2 和 MySQL 上都没有那个字段。
     *   想知道是不是唯一索引，去看 /db/keys（TABLE_CONSTRAINTS 里的 UNIQUE 类型）。
     *   （MySQL 的 information_schema.STATISTICS 有 NON_UNIQUE 列，但 H2 没有同名字段，
     *     为了"一份请求两边都能打"，这里只挑两边都有的字段。）
     */
    @GetMapping("/indexes")
    public Result<List<Map<String, Object>>> indexes() {
        // ⭐ MySQL 和 H2 的"索引元数据"放在**不同的表**里（真机实测出来的差异）：
        //     MySQL → INFORMATION_SCHEMA.STATISTICS（字段名 NON_UNIQUE / SEQ_IN_INDEX / COLUMN_NAME）
        //     H2    → INFORMATION_SCHEMA.INDEX_COLUMNS（字段名 ORDINAL_POSITION / COLUMN_NAME）
        //   ⭐ 这里**不是**为了"炫技兼容"，而是因为课程要求"同一份请求两边都能打" ——
        //     否则学生换到 MySQL 后这个接口直接 500，验收就没法做了。
        //   📌 注意我**顺手把 MySQL 的 NON_UNIQUE 也查出来了**（H2 那套没有这个字段）：
        //      0 = 唯一索引，1 = 普通索引。这是 MySQL 侧更直观的一个信息。
        if (mysql) {
            return Result.ok(jdbc.queryForList("""
                    SELECT TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX AS ORDINAL_POSITION,
                           COLUMN_NAME, NON_UNIQUE
                    FROM INFORMATION_SCHEMA.STATISTICS
                    WHERE TABLE_SCHEMA = """ + schemaName + """
                    ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX
                    """));
        }
        return Result.ok(jdbc.queryForList("""
                SELECT TABLE_NAME, INDEX_NAME, ORDINAL_POSITION, COLUMN_NAME
                FROM INFORMATION_SCHEMA.INDEX_COLUMNS
                WHERE TABLE_SCHEMA = """ + schemaName + """
                ORDER BY TABLE_NAME, INDEX_NAME, ORDINAL_POSITION
                """));
    }

    /**
     * 每一条约束的"清单" —— 今天最该盯的一张表
     *
     * CONSTRAINT_TYPE 有四种：
     *   PRIMARY KEY  主键
     *   UNIQUE       唯一（⭐ 在 MySQL 里它就是一个唯一索引）
     *   FOREIGN KEY  外键
     *   CHECK        检查约束（MySQL 8 才真正生效；MySQL 5.7 会静默忽略！）
     *
     * ⭐ 对照验收单：sku 上应该有 1 个 PK + 1 个 UNIQUE(`uk_sku_code`) + 2 个 CHECK；
     *   orders 上应该有 1 个 PK + 1 个 FK + 2 个 CHECK。
     *   ⚠️ 注意 `uk_sku_code_alive` 和 `uk_audit_action_detail` 是"单独的 CREATE UNIQUE INDEX"建的，
     *     在 MySQL 里它们**会**作为 UNIQUE 约束出现在这张表里；
     *     在 H2 里则出现在 /db/indexes 里、**不一定**出现在 TABLE_CONSTRAINTS 里 —— 
     *     这是两个数据库对"索引 vs 约束"归类不同（真机实测差异，别以为是漏建了）。
     */
    @GetMapping("/keys")
    public Result<List<Map<String, Object>>> keys() {
        return Result.ok(jdbc.queryForList("""
                SELECT TABLE_NAME, CONSTRAINT_NAME, CONSTRAINT_TYPE
                FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
                WHERE TABLE_SCHEMA = """ + schemaName + """
                ORDER BY TABLE_NAME, CONSTRAINT_TYPE, CONSTRAINT_NAME
                """));
    }
}