# JDBC 知识点总结（Day21 / 26 / 27 / 28 四天合一篇）

> 上一篇 `MySQL知识点总结.md` 只讲 SQL，**不含 Java**；这篇补上"Java 怎么跟 MySQL 说话"。
> 对应进度：Day21（第一次连库）、Day26（PreparedStatement / 结果集 / 聚合）、Day27（DAO 分层 + 事务）、Day28（影响行数 / 隔离级别 / 连接池）。
> 标记：⭐ 必背 ｜ ⚠️ 你踩过的坑 ｜ 🔬 Day21~28 真机验证过的结论

**怎么读**

| 时间 | 只看 |
|---|---|
| 10 分钟 | 第 2 节五步模板 + 第 9 节报错表 |
| 半小时 | 第 1~6 节（模板 + CRUD）|
| 一小时 | 全篇，跳过第 10 节 |
| 面试前 | 第 10 节 + 第 11 节自测 |

---

## 1. 全景：一行 Java 代码怎么变成一条 SQL

```
Java 代码
  → JDBC 接口（java.sql.*，JDK 自带）
  → MySQL 驱动（mysql-connector-j，一个 jar）
  → TCP 3306
  → mysqld 进程
  → 你的库/表
```

⭐ **一句话**：JDBC 是"标准插座"，驱动是"转接头"，MySQL 是"电器"。换数据库只换驱动，代码基本不动。

JDBC 里就六个零件，全部背下来：

| 零件 | 干什么 |
|---|---|
| `DriverManager` | 按 URL 找驱动、建连接 |
| `Connection` | 一条到数据库的连接（事务也挂在它身上）|
| `PreparedStatement` | 带 `?` 的 SQL，能防注入 |
| `ResultSet` | 查询结果（一个只能往前走的游标）|
| 事务三件套 | `setAutoCommit(false)` / `commit()` / `rollback()` |
| `DataSource` / 连接池 | 复用连接，别每次新建 |

---

## 2. ⭐ 五步模板（肌肉记忆）

```java
String url = "jdbc:mysql://127.0.0.1:3306/day21?useSSL=false&characterEncoding=UTF-8";
try (Connection conn = DriverManager.getConnection(url, "root", "root");
     PreparedStatement ps = conn.prepareStatement("SELECT id, name FROM student WHERE id = ?")) {
    ps.setInt(1, 1);
    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " " + rs.getString("name"));
        }
    }
}
```

五步：**拿连接 → 写 SQL（带 `?`）→ 填参数 → 执行 → 处理结果**。`try-with-resources` 自动关（关闭顺序：ResultSet → Statement → Connection）。

⚠️ 三个细节：

- `Class.forName("com.mysql.cj.jdbc.Driver")` **JDBC 4 之后可以省**（驱动 jar 里有 SPI 自动注册）；写上也不错，面试会问"为什么"。
- URL 里的 `characterEncoding=UTF-8` 是 **Java 侧**的名字，**写 `utf8mb4` 会连不上**（Day35 实测）。
- 端口、库名、账号密码写错，报的错完全不一样（见第 9 节）。

---

## 3. `PreparedStatement` vs `Statement`（面试必问）

| | `Statement` | `PreparedStatement` |
|---|---|---|
| 写法 | 字符串拼 SQL | SQL 里写 `?`，再 `setXxx` 填 |
| 注入 | ❌ `' OR '1'='1` 直接进库 | ✅ 参数永远当"值"，不参与语法 |
| 类型 | 全靠手拼引号 | `setInt` / `setString` / `setObject` 自动处理 |
| 性能 | 每次重新解析 | 预编译，可复用执行计划 |

⭐ 记住：**能用 `?` 就别拼字符串**，这不是风格问题，是安全问题。

⚠️ 唯一必须拼的场景：`LIKE '%关键字%'`、表名/列名（不能参数化）—— 前者拼参数值 `"%"+kw+"%"` 再 `setString`，后者只能白名单校验。

---

## 4. `ResultSet`：只能往前走的游标

```java
while (rs.next()) {          // 有下一行 → true，并移动游标
    int id = rs.getInt("id");
    String name = rs.getString("name");
}
```

⭐⭐ **你踩过的最贵的一个坑（Day27 Ex3）**：

```java
if (rs.next() == false) return list;   // ← 这一下已经把第 1 行吃掉了
while (rs.next()) { ... }               // ← 从第 2 行开始读
```

`rs.next()` = "往下走一格 + 告诉你有没有踩到"。判断和取值**必须共用同一次调用**，不能连调两次。

⭐ 其它规矩：

- 按**列名**取（`getInt("id")`），别按序号（加列就错位）；
- 判断空值先 `rs.getObject("x") == null` 或 `rs.wasNull()`；
- 取完必须关（`try-with-resources`）。

---

## 5. CRUD 与"影响行数"

```java
// 增 / 删 / 改：都用 executeUpdate，返回 int（影响了几行）
int n = ps.executeUpdate();
```

⭐ **`0` 不是异常**：`UPDATE ... WHERE id = 999` 返回 0，表示"没有这行"，调用方要自己判断（Day28 Ex1 就是练这个）。

```java
// 拿自增主键（两种写法）
ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
ps.executeUpdate();
try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); int id = keys.getInt(1); }

// 或者查 SQL：SELECT LAST_INSERT_ID();
```

⭐ **聚合下推**：`COUNT/SUM/AVG/MAX` 让数据库算（`SELECT COUNT(*)`），别 `SELECT *` 拉回 Java 再循环 —— 少传几万行。

---

## 6. 批处理：`addBatch` / `executeBatch`

```java
try (PreparedStatement ps = conn.prepareStatement("INSERT INTO student(name, score) VALUES (?, ?)")) {
    for (Student s : list) {
        ps.setString(1, s.getName());
        ps.setInt(2, s.getScore());
        ps.addBatch();              // 先攒着
    }
    int[] rows = ps.executeBatch(); // 一次性发过去
}
```

⭐ 返回值是 **`int[]`（每条的影响行数）**，不是"总行数"；想要总数自己累加。

---

## 7. ⭐⭐ 事务：三件套 + 一个铁律

```java
Connection conn = ds.getConnection();
try {
    conn.setAutoCommit(false);                 // ① 关自动提交 = 开启事务
    deduct(conn, from, amount);
    add(conn, to, amount);
    conn.commit();                             // ② 全成功才提交
} catch (Exception e) {
    conn.rollback();                           // ③ 任何一步失败，全撤
    throw e;
} finally {
    conn.setAutoCommit(true);                  // ④ 还回池子前恢复默认
    conn.close();
}
```

⭐⭐ **铁律：同一个事务里的所有 SQL 必须用同一个 `Connection`。**
连接池里 `ds.getConnection()` 每次给的可能是不同连接 —— 两个连接 = 两个事务，回滚不了对方。

其他要点：

- **受检异常不会自动回滚**（Spring 的 `@Transactional` 默认只回滚 `RuntimeException`，Day34 学过）；
- `commit()` 之前别人看不到你的修改（隔离级别决定"看到多少"）；
- 事务要短：别在事务里发 HTTP、等用户输入、睡 3 秒。

---

## 8. DAO 分层（Day27 的骨架）

```
main（交互）
 └── Service（业务：转账、校验、事务边界）
      └── DAO（只写 SQL：增删改查）
           └── 实体类（Student）+ 表（student）
```

| 层 | 只干一件事 | 不该出现的东西 |
|---|---|---|
| main | 读输入、打印 | SQL |
| Service | 业务规则 + 事务 | `rs.getString` |
| DAO | SQL + 行映射 | 业务判断 |
| 实体 | 装数据 | 任何 SQL |

⭐ 两个模板化技巧：

- **`mapRow(ResultSet) → 实体` 只写一次**，所有查询复用；
- **查不到返回空 `List`，不返回 `null`**（调用方少一层判空，也不会 NPE）。

---

## 9. 连接池：为什么不能"每次新建"

一次 `getConnection()` 的真实成本：TCP 三次握手 + 认证 + 建会话，**几十毫秒**；高并发下直接把数据库打满。

| | 每次新建 | 连接池 |
|---|---|---|
| 拿连接 | 30~200 ms | 微秒级（借一个现成的）|
| 上限 | 无，能把库拖死 | `maximumPoolSize` 卡住 |
| 归还 | 关掉就没了 | `close()` = **还给池子**（不是真关）|

⭐ HikariCP 四个参数（SpringBoot 默认就是它）：

| 参数 | 意思 | 经验值 |
|---|---|---|
| `maximumPoolSize` | 最多几个连接 | 10~20（不是越大越好）|
| `minimumIdle` | 最少留几个 | 跟 max 一样，避免抖动 |
| `connectionTimeout` | 借不到等多久 | 3 秒，超时抛异常 |
| `idleTimeout` | 空闲多久回收 | 10 分钟 |

⚠️ **连接泄漏**：借了不还 → 池子被借空 → 后面全部超时。所以 `finally` / `try-with-resources` 必须还。

🔬 Day28 手写 MiniPool 的结论：**借出去的是"代理壳"，真连接在池子里复用** —— 所以 `c5 == c2` 是 `false`，但底层连接是同一个。判断"复用没复用"要看池子剩余数量，不要看对象相等。

---

## 10. 隔离级别（JDBC 里怎么设）

```java
conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);  // MySQL 默认
```

| 级别 | 脏读 | 不可重复读 | 幻读 |
|---|---|---|---|
| READ UNCOMMITTED | ✅ 会 | ✅ 会 | ✅ 会 |
| READ COMMITTED | ❌ | ✅ 会 | ✅ 会 |
| **REPEATABLE READ（默认）** | ❌ | ❌ | ❌（快照读靠 MVCC，当前读靠间隙锁）|
| SERIALIZABLE | ❌ | ❌ | ❌（全加锁，最慢）|

⭐ 背三句：
1. **脏读** = 读到别人没提交的；
2. **不可重复读** = 同一行读两次不一样（别人改了）；
3. **幻读** = 同一条件查两次，行数变了（别人插了）。

---

## 11. 报错速查（原文 → 病因 → 修法）

| 报错原文 | 病因 | 修法 |
|---|---|---|
| `Communications link failure` | MySQL 没启动 / 端口不对 | 双击 `启动MySQL.bat`，确认 3306 |
| `Access denied for user 'root'@'localhost'` | 密码错 / 没权限 | 核对账号密码；`GRANT` 授权 |
| `Unknown database 'day21'` | 库没建 | `CREATE DATABASE day21` |
| `Table 'day21.student' doesn't exist` | 表名/库名拼错 | `SHOW TABLES` 对一遍 |
| `Duplicate entry '...' for key 'uk_name'` | 唯一索引撞车 | 先查再插，或改成 409（Day35）|
| `No suitable driver found` | 驱动 jar 没进 classpath | Maven 依赖 / `-cp` 带驱动 |
| `Public Key Retrieval is not allowed` | 8.x 认证插件 | URL 加 `allowPublicKeyRetrieval=true` |
| `Incorrect string value: '\xF0\x9F...'` | 存 emoji，列是 utf8(3 字节) | 改 `utf8mb4` |
| 中文变 `???` | 连接串/表字符集不对 | `characterEncoding=UTF-8` + `utf8mb4` |
| `Column 'name' in field list is ambiguous` | 联表两表同名列 | 加表别名 `s.name` |

⭐ 排错顺序永远是：**先确认 MySQL 在跑 → 再确认库表存在 → 再看账号权限 → 最后才怀疑代码**。

---

## 12. 面试三句话（背）

1. **JDBC 流程**：加载驱动 → 建连接 → 预编译 SQL → 设参数 → 执行 → 处理结果集 → 关闭（try-with-resources）。
2. **为什么用 PreparedStatement**：`?` 占位，参数不参与 SQL 解析，**防 SQL 注入**，还能预编译复用。
3. **事务怎么保证**：同一个 `Connection` 上 `setAutoCommit(false)` → 业务全部成功 `commit()`，任一失败 `rollback()`，连接池里用完要恢复自动提交再归还。

---

## 13. 自测：问答 12 题（先答，再点开）

1. `Statement` 和 `PreparedStatement` 最本质的区别是什么？
2. `executeQuery` 和 `executeUpdate` 各返回什么？
3. `rs.next()` 调两次会发生什么？你踩过哪个 bug？
4. 为什么"查不到"要返回空 `List` 而不是 `null`？
5. `executeBatch()` 返回的 `int[]` 是什么？想要总行数怎么办？
6. 事务里两个 DAO 方法，各用 `ds.getConnection()` 拿连接，会怎样？
7. 为什么 `finally` 里要 `setAutoCommit(true)`？
8. 连接池的 `close()` 到底做了什么？
9. `maximumPoolSize` 是不是越大越好？为什么？
10. `Repeatable Read` 能防幻读吗？靠什么？
11. `UPDATE ... WHERE id = 999` 返回 0，是异常吗？要不要 try/catch？
12. 中文乱码，你要查哪三个地方？

<details>
<summary>点开看答案</summary>

1. 预编译 + 参数化：`?` 的值永远当数据，不参与语法，所以防注入。
2. `executeQuery` → `ResultSet`（查）；`executeUpdate` → `int` 影响行数（增删改）。
3. 每次 `next()` 都把游标往下挪一格。`if (rs.next()==false) return;` 后再 `while(rs.next())` 会吃掉第一行。
4. 空 List 让调用方直接 `for`，少一层判空，也避免 NPE。
5. 每条语句的影响行数数组。总数自己 `for` 累加。
6. 两个连接 = 两个独立事务，`rollback()` 只能撤自己那条，另一条已经提交。
7. 池子会把连接借给下一个请求，自动提交状态必须回到默认，否则下一个人以为在自动提交。
8. 不是真关，是"还给池子"，连接对象被重置后复用。
9. 不是。数据库连接数有限，池太大只会把数据库压垮、上下文切换变多；一般 10~20。
10. 快照读靠 MVCC 不会幻读；当前读（`FOR UPDATE`）靠 next-key lock（间隙锁）。
11. 不是异常，是"没这行"。要自己判断返回值决定成功/失败。
12. 连接串 `characterEncoding`、库/表 `charset`、客户端 `--default-character-set`（再加一个：列的类型）。

</details>

---

## 14. 自测：代码题 6 道（不配答案，写完喊我检查）

> 每题都在 `java/day2x` 那种独立目录里写，别改以前的作业。

1. **`Db` 工具类**：`getConnection()` / `close(AutoCloseable...)`，URL 从常量读；连不上时打印一句人话提示。
2. **模糊查询（防注入）**：按姓名关键字查学生，返回 `List<Student>`；要求用 `PreparedStatement`，关键字拼进**参数值**而不是 SQL 字符串。
3. **DAO 三件套**：`StudentDao` 里写 `add(Student)` / `findAll()` / `countByClass(String)`；`findAll` 用 `mapRow` 抽出来复用。
4. **事务转账**：`transfer(fromId, toId, amount)`，余额不足抛异常并回滚；要求打印转账前后两张卡的余额和对账总额（总额必须不变）。
5. **批处理**：一次插入 1000 条学生，打印 `executeBatch()` 返回数组的长度和总和，对比单条循环插入的耗时。
6. **迷你连接池**：`MiniPool` 有 `borrow()` / `giveBack()` 和上限 3；借满第 4 个返回 `null`；用动态代理包一层，打印"借出/归还"日志。

---

## 附：和已有材料的关系

| 材料 | 内容 |
|---|---|
| `复习/MySQL知识点总结.md` | 纯 SQL（数据类型 / 约束 / 查询 / 事务 / 索引结论）|
| `java/day21/知识点.md` | JDBC 第一次连库（本篇第 1~5 节的原文）|
| `java/day26/知识点.md` | PreparedStatement / ResultSet / 聚合下推 |
| `java/day27/知识点.md` | DAO 分层 + `mapRow` + 批处理 + 事务 |
| `java/day28/知识点.md` | 影响行数 / 隔离级别 / 连接池 + 动态代理 |
| `java/day35/知识点.md` | 换真 MySQL + 约束债（唯一索引撞车 → 409）|
