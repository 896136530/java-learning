# MySQL 知识点总结（单篇 · 照着顺序复习）

> 一篇只讲 MySQL / SQL，不含 JDBC 和 Java（那是另一条线）。
> 对应你的进度：Day25（库表 CRUD）、Day28（隔离级别）、Day35（约束/字符集/换真库）；**索引细讲在 Day36~37**，本篇先给结论。
> 标记约定：⭐ 必背 ｜ ⚠️ 踩过的坑 ｜ 🔬 本机真机实测过的结论（8.4.5）

**怎么读（别从头背到尾）**

| 你有多少时间 | 只看这些 |
|---|---|
| 10 分钟 | 第 14 节速查卡 + 第 12 节报错表 |
| 半小时 | 第 5、6、7 节（查询、分组、连表）+ 第 8 节事务结论 |
| 一小时 | 全篇，跳过第 13 节面试题（那节是最后冲刺用） |
| 面试前 | 第 13 节 16 问 + 第 15 节自测 |

**五句口诀（忘了就看这里）**

1. **增删改查**：`INSERT` 增 / `DELETE` 删 / `UPDATE` 改 / `SELECT` 查。
2. **写的时候**：`SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT`。
3. **跑的时候**：`FROM → WHERE → GROUP BY → 聚合 → HAVING → SELECT → ORDER BY → LIMIT`（所以 WHERE 里不能起别名、不能放聚合）。
4. **有 WHERE 才有命**：UPDATE / DELETE 先写 WHERE，再跑 SELECT 验证。
5. **四个默认动作**：判空用 `IS NULL`、字符串用单引号、钱用 `DECIMAL`、中文用 `utf8mb4`。

---

## 0. 先记住这张图

```
mysqld 进程（监听 3306）
└── 数据库 database（仓库，如 day25）
    └── 表 table（表格，如 student）
        ├── 行 row（一条记录）
        └── 列 column（字段）
```

初学者最容易懵的点：**"连数据库"其实连的是那个进程**，库只是进程里的一个目录。所以 `mysql -u root -p` 进去之后，还要 `USE day25;` 才算"进了我的库"。

SQL 按用途分五类（面试会问分类）：

| 类 | 全称 | 语句 |
|---|---|---|
| DDL | 定义 | `CREATE` `ALTER` `DROP` `TRUNCATE` |
| DML | 操作数据 | `INSERT` `UPDATE` `DELETE` |
| DQL | 查询 | `SELECT` |
| DCL | 权限 | `GRANT` `REVOKE` |
| TCL | 事务 | `START TRANSACTION` `COMMIT` `ROLLBACK` `SAVEPOINT` |

---

## 1. 数据类型：选错就改不动

| 类型 | 存什么 | 关键点 |
|---|---|---|
| `INT` / `BIGINT` | 整数、id | 用户量过 21 亿才需要 BIGINT；一般 INT 够 |
| `VARCHAR(n)` | 变长短文本 | n 是**字符数**不是字节数；按实际需要给，别一律 255 |
| `CHAR(n)` | 定长（性别、状态码） | 不够长会用空格补；短而定长的用它更省 |
| `DECIMAL(p,s)` | 钱、精确小数 | 总位数 p、小数位 s；`DECIMAL(10,2)` 上限 99999999.99 |
| `DOUBLE` / `FLOAT` | 科学计算 | ⚠️ **绝不用来存钱**：近似值，对账差一分钱能查一晚上 |
| `DATE` / `DATETIME` | 日期 / 日期时间 | 时间别用字符串存，否则比较、排序、`DATEDIFF` 全废 |
| `TIMESTAMP` | 时间戳 | 只到 2038 年，且带时区转换；一般用 DATETIME |
| `TEXT` | 长文本（文章、备注） | 不能有默认值，尽量别放主表（拆出去） |
| `TINYINT(1)` | 布尔（0/1） | MySQL 没有真正的 BOOLEAN，`BOOLEAN` 是它的别名 |

三条硬规矩：

1. **钱用 DECIMAL**。🔬 实测 `0.1+0.2` 用 DOUBLE 显示 0.3，但 DECIMAL 才是精确的 0.30，多笔累加后差异会显形。
2. **中文/emoji 一律 utf8mb4**（详见第 10 节）。
3. **类型和值要对上**：往 INT 列塞 `'abc'`，严格模式直接 `1366 Incorrect integer value`；`DECIMAL(5,2)` 塞 1000.00 报 `1264 Out of range`（小数位超出只是四舍五入，不报错）。

---

## 2. 约束：数据库帮你守的底线（Day35 的债）

| 约束 | 作用 | 违约时真机报错（背这个） |
|---|---|---|
| `PRIMARY KEY` | 一行唯一身份证，不能空 | 重复：`1062 Duplicate entry '1' for key 'PRIMARY'` |
| `NOT NULL` | 不许空 | `1048 Column 'name' cannot be null` |
| `UNIQUE` | 不许重复（可空，且多行 NULL 不算重复） | `1062 Duplicate entry '张三' for key 't.name'` |
| `DEFAULT` | 不写就用默认值 | 不报错，静默生效 |
| `CHECK` | 值必须满足条件（8.0.16+ 真生效） | `3819 Check constraint 'ck_score' is violated.` |
| `FOREIGN KEY` | 引用的父行必须存在 | `1452 Cannot add or update a child row: a foreign key constraint fails` |

写法（建表时一次写全）：

```sql
CREATE TABLE student (
  id    INT PRIMARY KEY AUTO_INCREMENT,
  name  VARCHAR(20) NOT NULL,
  score DECIMAL(5,2) DEFAULT 0 CHECK (score BETWEEN 0 AND 100),
  cls   VARCHAR(20),
  CONSTRAINT uk_name UNIQUE (name)          -- 命名后用得上：能按名字删约束
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

四个容易混的点：

- **主键 vs 唯一**：主键只能一个、不能空；唯一可以多个、可以空（且 NULL 不参与去重）。
- **唯一索引就是 UNIQUE 约束**：`UNIQUE KEY uk_name (name)` 建索引的同时就约束了。
- **外键生产里基本不用**（分库分表、性能、运维都嫌它），靠"逻辑外键"（只存 user_id，不建约束）+ 应用层校验；但面试会问，得知道。
- **业务校验 ≠ 数据库约束**：唯一/非空/外键违约到 Java 层是 `DataIntegrityViolationException` 家族（唯一那条更具体，是 `DuplicateKeyException`）；但 **`CHECK` 违约（3819 / SQLState `HY000`）不在这个家族**，Spring 抛的是 `UncategorizedSQLException`——只 catch 前者，CHECK 违约就漏网变 500。

### ⚠️ 逻辑删除和 UNIQUE 是天生一对冤家

表上 `UNIQUE(name)`，删除改成 `deleted=1` 后，**同名的新数据插不进来**——🔬 实测直接 `1062 Duplicate entry '王五' for key 't.name'`，因为旧行还占着那个名字。

| 方案 | 做法 | 评价 |
|---|---|---|
| ✅ 推荐 | `UNIQUE(name, deleted)`，`deleted BIGINT DEFAULT 0`，删除时 `SET deleted = id` | 实测通过：`王五 deleted=1` 与 `王五 deleted=0` 共存 |
| ⚠️ 坑 | `UNIQUE(name, deleted_at)` 用 NULL 表示未删 | 🔬 实测**两行 NULL 都能插进去**（唯一索引不管 NULL），防不住 |
| 能用 | 删除时把 name 改成 `王五#1712`（加 id 后缀） | 简单但要额外维护字段 |
| 也能用 | 不加唯一索引，应用层查重 | 并发下会击穿，不推荐 |

---

## 3. DDL：建库、建表、改表

```sql
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET = utf8mb4;
USE demo;
SHOW DATABASES;             -- 有哪些库（mysql/sys/information_schema 是系统的，别动）
SHOW TABLES;                -- 当前库有哪些表
DESC student;               -- 看表结构（等价 SHOW COLUMNS FROM student）
SHOW CREATE TABLE student;  -- 看建表原句（排查字符集/引擎最快）
DROP DATABASE demo;         -- ⚠️ 整库删光，只在自己练手的库上用
```

改表四件套：

```sql
ALTER TABLE student ADD COLUMN age INT DEFAULT 0;            -- 加列
ALTER TABLE student MODIFY COLUMN name VARCHAR(50) NOT NULL; -- 改类型/约束（不改名）
ALTER TABLE student CHANGE COLUMN cls class_name VARCHAR(20);-- 改名 + 改类型
ALTER TABLE student DROP COLUMN age;                         -- 删列
ALTER TABLE student RENAME TO student2;                      -- 改表名
ALTER TABLE student ADD UNIQUE KEY uk_name (name);           -- 补索引/约束
```

⚠️ **ALTER 是隐式提交**：事务里 `START TRANSACTION; INSERT; ALTER; ROLLBACK;` —— 🔬 实测那一行**还在**，回滚白做。所以"事务里混 DDL"是死路。

### 删数据三兄弟（面试必问）

| | `DELETE FROM t WHERE ...` | `TRUNCATE TABLE t` | `DROP TABLE t` |
|---|---|---|---|
| 类别 | DML | DDL | DDL |
| 能带 WHERE | ✅ | ❌ 整表清空 | ❌ 连表带数据 |
| 能回滚 | ✅（在事务里） | ❌ 隐式提交 | ❌ |
| 自增 | **不重置**（🔬 删完再插 id=4） | **重置**（🔬 清空后 id=1） | — |
| 速度 | 慢（逐行、写 undo/日志） | 快（重建表） | 快 |
| 触发器 | 触发 | 不触发 | — |

一句话：**清空整表用 TRUNCATE，删几条用 DELETE，表都不要了用 DROP。**

---

## 4. DML：CRUD 四句（必须有肌肉记忆）

```sql
SELECT 列 FROM 表 WHERE 条件;                                  -- 查
INSERT INTO 表 (列1, 列2) VALUES (值1, 值2);                    -- 增
UPDATE 表 SET 列 = 值 WHERE 条件;                               -- 改
DELETE FROM 表 WHERE 条件;                                     -- 删
```

补几个好用的变体：

```sql
INSERT INTO student (name, score, cls) VALUES ('张三',88,'一班'),('李四',95,'二班'); -- 一条插多行（比多条快）
INSERT INTO student (name) SELECT name FROM other;                                   -- 从别的表搬
INSERT INTO student (name) VALUES ('王五')
  AS new ON DUPLICATE KEY UPDATE score = new.score;      -- 有则更新（8.4 推荐 AS new 写法）
```

### ⚠️ 全篇最贵的坑：UPDATE / DELETE 忘写 WHERE

后果不是"报错"，是**静默毁库**——整表分数一起改、整表删空。养成三个动作：

1. 先跑 `SELECT * FROM 表 WHERE 同条件;` 看命中几行，再改成 UPDATE/DELETE。
2. 危险操作前 `START TRANSACTION;`，看完影响行数再 `COMMIT`。
3. 手抖时加保险：`UPDATE ... LIMIT 1;`（先改一条验证语法）。

另外两条：字符串用**单引号** `'一班'`；判断空用 `IS NULL`，`= NULL` 永远 0 行（🔬 实测 0 行）。

> ⚠️ **纠个错**：网上和你 Day25 课件里说"双引号会被当列名、报 Unknown column"——那是**开了 `ANSI_QUOTES`** 才会。🔬 本机默认 `SELECT "abc"` 直接就是字符串 `abc`，`WHERE name = "张三"` 也能查到。**但规范写法仍然只用单引号**，因为换库（PostgreSQL）或换 sql_mode 时双引号立刻翻车，而且可读性差。

---

## 5. SELECT 的完整骨架

### 5.1 书写顺序 ≠ 执行顺序（面试高频）

```
你写的：  SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT
它跑的：  FROM/JOIN → WHERE → GROUP BY → 聚合 → HAVING → SELECT → DISTINCT → ORDER BY → LIMIT
```

背执行顺序的用处（两个面试点都藏在里面）：

| 现象 | 原因 |
|---|---|
| `WHERE` 里不能用 `SELECT` 起的别名 | WHERE 比 SELECT 先执行 |
| `WHERE` 里不能用 `COUNT(*)` 这类聚合 | 聚合发生在 GROUP BY 之后、WHERE 之后 |
| `ORDER BY` 能用别名 | ORDER BY 在 SELECT 之后执行 |

### 5.2 WHERE 的运算符

| 写法 | 意思 | 注意 |
|---|---|---|
| `=` `<>` `!=` `>` `<` `>=` `<=` | 比较 | 字符串要引号，日期用 `'2026-10-28'` |
| `AND` `OR` `NOT` | 逻辑 | `AND` 优先级高于 `OR`，混用必须加括号 |
| `BETWEEN a AND b` | 闭区间 | 两边都包含 |
| `IN (1,2,3)` / `NOT IN` | 集合 | ⚠️ `NOT IN` 遇到 NULL 会整体失效（返回 0 行） |
| `LIKE '张%'` / `'%张%'` / `'_三'` | 模糊 | `%` 任意多字符，`_` 一个字符；前导 `%` 索引失效 |
| `IS NULL` / `IS NOT NULL` | 判空 | 不能写 `= NULL` |
| `EXISTS (子查询)` | 存在 | 与 IN 的取舍见第 7 节 |

### 5.3 NULL 三值逻辑（最容易翻车的地方）

NULL 不是 0、不是空串，是"不知道"。任何和 NULL 的比较结果都是 **unknown**，等于不成立。

```sql
SELECT COUNT(*) FROM student WHERE score = NULL;   -- 永远 0 行
SELECT COUNT(*) FROM student WHERE score IS NULL;  -- 正确写法
```

| 陷阱 | 结果 |
|---|---|
| `NULL = NULL` | unknown（不是 true） |
| `COUNT(*)` | 数行数，含 NULL 行 |
| `COUNT(列)` | **跳过 NULL** |
| `SUM` / `AVG` / `MAX` / `MIN` | 全部跳过 NULL；`AVG = SUM / COUNT(非空)`，不是除以总行数 |
| `NULL + 1` / `CONCAT('a', NULL)` | 结果是 NULL |
| `ORDER BY 列` | NULL 排最前（升序时） |
| `GROUP BY 列` | 所有 NULL 归成一组 |

兜底函数：`IFNULL(列, 默认值)`、`COALESCE(a, b, c)`（取第一个非 NULL）。

### 5.4 排序与分页

```sql
SELECT name, score FROM student
WHERE cls = '一班'
ORDER BY score DESC, name ASC    -- 多列排序：先按分数降序，同分按姓名升序
LIMIT 10 OFFSET 20;              -- 跳过 20 条取 10 条（页码 p：OFFSET (p-1)*size）
```

- `DESC` 降序、`ASC` 升序（默认），**只对紧挨着的那个列生效**。
- 🔬 中文排序：默认排序规则 `utf8mb4_0900_ai_ci` 按 **Unicode 码点**排，不是拼音——实测"孙八 张 李四 王五"（孙 5B59 < 张 5F20 < 李 674E < 王 738B）。要拼音顺序就 `ORDER BY CONVERT(name USING gbk)`。
- ⚠️ **深分页**：`LIMIT 100000, 10` 要先扫 100010 行才丢前 10 万行。优化：记上一页最大 id，`WHERE id > 上一页最后id ORDER BY id LIMIT 10`（游标分页）；或延迟关联 `JOIN (SELECT id ... LIMIT ...) t`。

---

## 6. 聚合与分组

| 函数 | 作用 | 备注 |
|---|---|---|
| `COUNT(*)` | 统计行数 | 最常用，InnoDB 会挑最小的索引扫 |
| `COUNT(列)` | 统计该列非 NULL 的个数 | 数"有成绩的人"就用它 |
| `COUNT(DISTINCT 列)` | 去重计数 | 🔬 实测一班/二班/四班 → 3 |
| `SUM` / `AVG` / `MAX` / `MIN` | 求和/平均/最值 | 全部忽略 NULL |
| `ROUND(x, 2)` | 保留两位 | 只改**显示**，不改变存储值 |
| `GROUP_CONCAT(列)` | 一组拼成一个字符串 | 如把班里姓名拼成 `张三,李四` |

完整写法：

```sql
SELECT cls AS 班级,
       COUNT(*) AS 人数,
       ROUND(AVG(score), 2) AS 平均分,
       MAX(score) AS 最高分
FROM student
WHERE score IS NOT NULL          -- 先筛行
GROUP BY cls                     -- 再按班级分堆，每堆算一遍聚合
HAVING COUNT(*) >= 2             -- 最后筛"组"（能用聚合结果）
ORDER BY 平均分 DESC
LIMIT 2;
```

- **WHERE 筛行、HAVING 筛组**，这是两者唯一区别。
- ⚠️ MySQL 8 默认开 `ONLY_FULL_GROUP_BY`：`SELECT` 里的非聚合列**必须**出现在 `GROUP BY` 里，否则 `1055 ... which is not functionally dependent on columns in GROUP BY clause`（🔬 实测）。`SELECT cls, name ... GROUP BY cls` 直接报错，不是"随便取一个值"。
- ⚠️ 分组结果的顺序**不保证**是"按平均分"——那只是巧合（你 Day25 就踩过，被组顺序骗了）。要顺序必须显式 `ORDER BY`。
- `WITH ROLLUP` 会多一行"总计"（了解即可）。

---

## 7. 多表查询：JOIN

| 类型 | 取什么 | 场景 |
|---|---|---|
| `INNER JOIN`（`JOIN` 同义） | 两表都匹配上的行 | 最常用 |
| `LEFT JOIN` | 左表全要，右表没有就补 NULL | "所有学生 + 他的班主任（可能没分班）" |
| `RIGHT JOIN` | 右表全要 | 一般改写成 LEFT JOIN，更好读 |
| `CROSS JOIN` | 笛卡尔积 | 基本只在造测试数据时用 |
| `FULL OUTER JOIN` | 两边全要 | ⚠️ MySQL **不支持**，用 `LEFT UNION RIGHT` 模拟 |

```sql
SELECT s.name, s.score, c.teacher
FROM student s
JOIN class_info c ON s.cls = c.cls     -- ON 是"接头暗号"
WHERE s.score >= 60
ORDER BY s.score DESC;
```

- 忘写 `ON` → 笛卡尔积（7 学生 × 3 班 = 21 行），数据全乱。
- 表别名（`s`、`c`）一为省字，二为**自连接**：`FROM emp e JOIN emp m ON e.manager_id = m.id`（员工查他的经理）。

### ⚠️ LEFT JOIN 里 ON 和 WHERE 不是一回事（经典面试题）

```sql
-- 写法 A：条件放 ON —— 左表全留，右表匹配不上的补 NULL
SELECT s.name, c.teacher FROM student s
LEFT JOIN class_info c ON s.cls = c.cls AND c.teacher = '王老师';

-- 写法 B：条件放 WHERE —— 补出来的 NULL 行被 WHERE 干掉，退化成 INNER JOIN
SELECT s.name, c.teacher FROM student s
LEFT JOIN class_info c ON s.cls = c.cls WHERE c.teacher = '王老师';
```

🔬 实测（4 个学生、只有三班有王老师）：A 返回 4 行 teacher 全 NULL；B 返回 0 行。**"LEFT JOIN 后 WHERE 过滤右表字段"= 白写 LEFT，一定要放 ON 里。**

### 子查询

```sql
SELECT name, score FROM student WHERE score > (SELECT AVG(score) FROM student);  -- 标量子查询
SELECT * FROM student WHERE cls IN (SELECT cls FROM class_info WHERE teacher = '李老师'); -- 列子查询
SELECT s.* FROM (SELECT * FROM student WHERE score >= 60) s;                    -- 派生表必须有别名
```

- `IN` vs `EXISTS`：子查询结果小用 `IN`，外表大、子表能走索引用 `EXISTS`（现代优化器常把两者优化成一样，别背死结论，说"看执行计划"最稳）。
- 派生表（FROM 后面那个括号）**必须起别名**，否则 1248 报错。

### 集合运算

`UNION` 去重 + 排序（有开销），`UNION ALL` 不去重（快）。列数、类型要对齐，`ORDER BY` 写在最后一句。

---

## 8. 事务

### ACID 四句话

| | 白话 |
|---|---|
| **A** 原子性 | 要么全成，要么全不成（转账不能只扣不加） |
| **C** 一致性 | 转账前后总金额不变（约束不被破坏） |
| **I** 隔离性 | 并发事务互不偷看中间状态 |
| **D** 持久性 | 提交了就落盘，断电也不丢 |

```sql
START TRANSACTION;          -- 或 BEGIN
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;                     -- 落盘
-- 出错：ROLLBACK;           -- 全撤
SAVEPOINT sp1; ... ROLLBACK TO sp1;   -- 部分回滚（了解）
```

⚠️ 三个必知前提：

1. 只有 **InnoDB** 支持事务（MyISAM 不支持，直接别用）。
2. `autocommit=1` 是默认值：**每条语句自动提交**，所以单条 UPDATE 手抖不能靠 `ROLLBACK` 挽回，只能靠备份/binlog 恢复——这正是第 4 节那个坑最贵的原因。
3. 事务里混 `ALTER`/`DROP` 等 DDL 会**隐式提交**（见第 3 节）。

### 隔离级别（Day28 学过，这里给结论表）

| 级别 | 脏读 | 不可重复读 | 幻读 | 说明 |
|---|---|---|---|---|
| `READ UNCOMMITTED` | ✅ 会发生 | ✅ | ✅ | 基本不用 |
| `READ COMMITTED` | ❌ | ✅ | ✅ | Oracle 默认 |
| `REPEATABLE READ` | ❌ | ❌ | ⚠️ 快照读不会，当前读靠间隙锁 | **MySQL 默认**（🔬 实测 `REPEATABLE-READ`） |
| `SERIALIZABLE` | ❌ | ❌ | ❌ | 全加锁，性能差 |

- 脏读：读到别人**没提交**的数据（人家一回滚，你就读到幻觉）。
- 不可重复读：同一行，两次读**值不一样**（别人中间提交了）。
- 幻读：同一条件，两次读**行数不一样**（别人插了新行）。
- MySQL 的 RR 比标准多一步：靠 **MVCC 快照**让普通 SELECT 读同一份快照（所以不会幻读），只有 `SELECT ... FOR UPDATE` 这类"当前读"才用 next-key lock（记录锁 + 间隙锁）。
- 切换：`SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;`，查：`SELECT @@transaction_isolation;`

⚠️ 长事务是事故之源：锁不释放、undo 堆积、主从延迟。原则：**事务里只放必须原子的 SQL，别在事务中间调接口/等用户输入。**

---

## 9. 索引（Day36~37 细讲，先记结论）

### 9.1 一句话原理

InnoDB 用 **B+ 树**：矮胖（3~4 层撑起千万行）、叶子节点连成链表（范围查询快）、非叶子只存键（一页塞更多）。

| 概念 | 意思 |
|---|---|
| 聚簇索引 | 主键索引，叶子**存整行数据**（一张表只有一个） |
| 二级索引 | 普通/唯一/联合索引，叶子存"索引列 + 主键" |
| 回表 | 二级索引找到主键，再回聚簇索引取整行（多一次查找） |
| 覆盖索引 | 要的列都在索引里，不用回表（`EXPLAIN` 的 `Extra: Using index`） |

### 9.2 索引类型与建法

```sql
CREATE INDEX idx_score ON student(score);                 -- 普通索引
CREATE UNIQUE INDEX uk_name ON student(name);             -- 唯一索引
CREATE INDEX idx_cls_score ON student(cls, score);        -- 联合索引（顺序有讲究）
CREATE FULLTEXT INDEX ft ON article(content);             -- 全文（中文要分词器，少用）
ALTER TABLE student DROP INDEX idx_score;                 -- 删索引
```

### 9.3 最左前缀（联合索引的灵魂）

索引 `(a, b, c)`：

| 查询条件 | 用得上吗 |
|---|---|
| `a = ?` | ✅ 用 a |
| `a = ? AND b = ?` | ✅ 用 a,b |
| `a = ? AND b = ? AND c = ?` | ✅ 全用 |
| `b = ?` / `b = ? AND c = ?` | ❌ 跳过最左列，全废 |
| `a = ? AND c = ?` | ⚠️ 只用 a，c 用不上 |
| `a > ? AND b = ?` | ⚠️ 只用 a（范围之后失效） |

记忆：**从左往右连续用，遇到范围就停。**

### 9.4 索引失效清单（面试常问，按出现频率排）

1. 列上做运算/函数：`WHERE YEAR(create_time) = 2026`、`WHERE id + 1 = 10`。
2. 隐式类型转换：`phone` 是 VARCHAR，却写 `WHERE phone = 13800138000`（数字）→ 全表扫。
3. `LIKE '%张'`（前导 %）——🔬 实测 `type=ALL`，`Extra: Using where`。
4. `OR` 连接的两列不是同一个索引。
5. `!=` / `NOT IN` / `IS NOT NULL`（可能失效，看数据分布）。
6. 联合索引跳过最左列。
7. 字符集不一致的 JOIN（`utf8mb4` 连 `utf8mb3`）会转成函数比较。

### 9.5 EXPLAIN 看四个列就够

```sql
EXPLAIN SELECT * FROM student WHERE name = '张三';
```

| 列 | 看什么 |
|---|---|
| `type` | 好坏：`const` > `eq_ref` > `ref` > `range` > `index` > **`ALL`（全表扫，要优化）** |
| `key` | 实际用了哪个索引；NULL = 没用索引 |
| `rows` | 预估扫描行数（越小越好） |
| `Extra` | `Using index` = 覆盖索引（好）；`Using filesort` = 额外排序（考虑加索引）；`Using temporary` = 用了临时表（分组/去重） |

⚠️ 索引不是越多越好：每个索引都占空间、拖慢写入（INSERT/UPDATE 要维护所有索引）。**给 WHERE / JOIN / ORDER BY 里高频出现的列建，别给每个列都建。**

---

## 10. 字符集：utf8 是"假 UTF-8"

| | 字节/字符 | 能存 emoji | 说明 |
|---|---|---|---|
| `utf8`（= `utf8mb3`） | 最多 3 | ❌ 🔬 实测 `1366 Incorrect string value: '\xF0\x9F\x98\x80'` | MySQL 历史遗留，**别用** |
| `utf8mb4` | 最多 4 | ✅ 实测 `CHAR_LENGTH=1, LENGTH=4` | 真 UTF-8 |

规范：**建库、建表、连接三处都要 utf8mb4**，缺一处就出 `??`。

```sql
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
-- 连接：客户端加 --default-character-set=utf8mb4（JDBC 里写 characterEncoding=UTF-8，写 utf8mb4 会报 Unsupported）
```

排序规则（collation）记住一个就够：`utf8mb4_0900_ai_ci`——`ai` = 大小写不敏感（'A' 和 'a' 相等，所以唯一索引也不许同时存 A 和 a），`ci` = 重音不敏感。

---

## 11. 常用函数速查

| 类别 | 函数 | 例子 |
|---|---|---|
| 字符串 | `CONCAT` `SUBSTRING` `CHAR_LENGTH` `LENGTH` `UPPER` `LOWER` `TRIM` `REPLACE` `LOCATE` | `CONCAT('a','b')` → `ab`；`CHAR_LENGTH('张三')`=2，`LENGTH`=6 |
| 数值 | `ROUND` `CEIL` `FLOOR` `ABS` `MOD` `TRUNCATE(x,d)` | `ROUND(3.456,2)`=3.46；`TRUNCATE(3.456,2)`=3.45（截断不四舍五入） |
| 日期 | `NOW()` `CURDATE()` `DATE_FORMAT` `DATEDIFF` `TIMESTAMPDIFF` `DATE_ADD` | `DATEDIFF('2026-12-31','2026-10-28')`=64 |
| 条件 | `IF(条件,a,b)` `IFNULL(x,默认)` `NULLIF` `CASE WHEN` | `CASE WHEN score>=90 THEN 'A' ELSE 'B' END` |
| 聚合 | `COUNT` `SUM` `AVG` `MAX` `MIN` `GROUP_CONCAT` | 见第 6 节 |
| 其他 | `CAST(x AS 类型)` `CONVERT` `DATABASE()` `VERSION()` `LAST_INSERT_ID()` | `LAST_INSERT_ID()` 拿刚插入的自增 id |

---

## 12. 报错速查（真机原文 → 病因 → 修法）

| 报错 | 病因 | 修法 |
|---|---|---|
| `1062 Duplicate entry 'x' for key 'uk_y'` | 唯一/主键重复 | 先查重再插，或 `ON DUPLICATE KEY UPDATE` |
| `1048 Column 'x' cannot be null` | 非空列塞了 NULL | 给值或设 DEFAULT |
| `1055 ... not in GROUP BY clause` | 开了 ONLY_FULL_GROUP_BY | 非聚合列进 GROUP BY 或套聚合函数 |
| `1054 Unknown column 'x' in 'where clause'` | 列名拼错/表别名没写/字符串用了双引号（ANSI_QUOTES 下） | 核对列名 |
| `1064 You have an error in your SQL syntax` | 语法错（最常见：关键字顺序、少逗号、少引号） | 看 `near '...'` 后面那段 |
| `1146 Table 'db.t' doesn't exist` | 库/表不存在或没 `USE` | `SHOW TABLES` |
| `1366 Incorrect string value` | 用 utf8mb3 存了 4 字节字符 | 改 utf8mb4 |
| `1264 Out of range value` | 超出类型范围 | 放宽类型或校验 |
| `1452 ... foreign key constraint fails` | 插了父表没有的 id | 先插父表或用逻辑外键 |
| `3819 Check constraint 'ck' is violated.` | CHECK 不满足 | 改值或改约束 |
| `1045 Access denied for user` | 账号/密码错 | 核对账号，别用 root/root 上生产 |
| `2003 Can't connect to MySQL server (10061)` | 服务没起/端口不对 | 先启动 mysqld（你本机就是这情况） |

---

## 13. 面试高频 16 问（一句话答案）

| 问题 | 一句话答案 |
|---|---|
| 事务四大特性 | 原子、一致、隔离、持久；InnoDB 靠 undo log（原子）、redo log（持久）、MVCC+锁（隔离）实现 |
| 四种隔离级别 | RU/RC/RR/SERIALIZABLE；MySQL 默认 RR |
| 为什么默认 RR | 历史上为了主从复制的 statement 格式不丢数据；现在也能配合 row 格式用 RC |
| 索引为什么用 B+ 树 | 矮胖（IO 次数少）、有序（范围/排序友好）、叶子链表、非叶子只存键 |
| 聚簇索引 vs 二级索引 | 聚簇叶子存整行（按主键组织表），二级叶子存主键值 |
| 回表 / 覆盖索引 | 二级索引查主键再回表取行；要的列都在索引里则不用回表 |
| 最左前缀 | 联合索引从最左列连续匹配，遇到范围查询后面的列失效 |
| 索引失效场景 | 列上函数/运算、隐式类型转换、前导 %、OR 跨索引、!=、跳过最左列 |
| COUNT(*) vs COUNT(1) vs COUNT(列) | 前两个都数所有行（InnoDB 优化后性能一样），COUNT(列) 跳过 NULL |
| WHERE vs HAVING | WHERE 分组前筛行、不能用聚合；HAVING 分组后筛组 |
| DELETE / TRUNCATE / DROP | 删行可回滚 / 清表重置自增不可回滚 / 删表 |
| 深分页怎么优化 | 游标 `WHERE id > last_id LIMIT n`，或延迟关联只查 id 再回表 |
| CHAR vs VARCHAR | 定长 vs 变长；CHAR 补空格、适合短定长；VARCHAR 按实际长度存 |
| 三范式 | 每列原子、非主键列完全依赖主键、非主键列不互相依赖；实际会为性能适度反范式 |
| 大表加字段/索引怎么做 | 8.0 的 online DDL 多数不锁表；超大表用 gh-ost / pt-online-schema-change |
| 慢查询怎么查 | 开 `slow_query_log` 抓 SQL → `EXPLAIN` 看 type/rows/Extra → 加索引或改 SQL |

---

## 14. 速查卡（抄在便签上）

```sql
-- 建库建表
CREATE DATABASE IF NOT EXISTS demo DEFAULT CHARSET = utf8mb4;
CREATE TABLE student (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(20) NOT NULL,
  score DECIMAL(5,2) DEFAULT 0,
  cls VARCHAR(20),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 增删改查
INSERT INTO student (name, score, cls) VALUES ('张三', 88, '一班');
SELECT * FROM student WHERE cls = '一班' AND score >= 60 ORDER BY score DESC LIMIT 10;
SELECT DISTINCT cls FROM student;                      -- 去重
UPDATE student SET score = score + 5 WHERE id = 1;
DELETE FROM student WHERE id = 1;

-- 分组
SELECT cls, COUNT(*) AS n, ROUND(AVG(score),2) AS avg_score
FROM student GROUP BY cls HAVING n >= 2 ORDER BY avg_score DESC;

-- 连表
SELECT s.name, c.teacher FROM student s
JOIN class_info c ON s.cls = c.cls WHERE s.score >= 60;

-- 左连 + 右表条件放 ON
SELECT s.name, c.teacher FROM student s
LEFT JOIN class_info c ON s.cls = c.cls AND c.teacher = '王老师';

-- 分页
SELECT * FROM student ORDER BY id LIMIT 10 OFFSET 0;

-- 事务
START TRANSACTION;
UPDATE account SET balance = balance - 100 WHERE id = 1;
UPDATE account SET balance = balance + 100 WHERE id = 2;
COMMIT;   -- 出问题 ROLLBACK;

-- 改表 / 索引 / 看执行计划
ALTER TABLE student ADD COLUMN age INT DEFAULT 0;
CREATE INDEX idx_cls_score ON student (cls, score);
EXPLAIN SELECT * FROM student WHERE name = '张三';
```

---

## 15. 自测 12 题（先答，再对答案）

1. `DELETE FROM student;` 之后自增值会回到 1 吗？`TRUNCATE` 呢？
2. `WHERE score = NULL` 为什么查不到数据？正确写法？
3. `utf8` 和 `utf8mb4` 的区别？建库漏写 charset 会怎样？
4. 联合索引 `(a,b,c)`，`WHERE b = 1 AND c = 2` 能用上索引吗？
5. `LEFT JOIN` 后把右表条件写在 `WHERE` 里，结果为什么变少？
6. `COUNT(*)` 和 `COUNT(score)` 在 `score` 有 NULL 时差在哪？
7. 事务里 `ALTER TABLE` 之后 `ROLLBACK`，改的数据还在吗？
8. MySQL 默认隔离级别？它能防幻读吗？靠什么？
9. 钱为什么用 `DECIMAL` 不用 `DOUBLE`？
10. 表有 `UNIQUE(name)` 又要逻辑删除，同名的新数据插不进去，怎么解决？
11. `SELECT cls, name FROM student GROUP BY cls;` 为什么报 1055？
12. `LIMIT 100000, 10` 慢在哪？怎么优化？

<details>
<summary>点开看答案</summary>

1. 不会（DELETE 是 DML，自增计数器留着，实测删完再插 id=4）；TRUNCATE 会重置（实测插回来 id=1）。
2. NULL 参与比较结果是 unknown，不是 true，所以 `= NULL` 永远 0 行；用 `IS NULL`。
3. utf8 最多 3 字节（utf8mb3），存不了 emoji（报 1366）；utf8mb4 才是真 4 字节。漏写 charset 会继承服务器默认，可能出现中文 `??` 或存不进 emoji。
4. 不能。跳过最左列 a，整个联合索引失效（除非优化器走覆盖索引扫描 index）。
5. LEFT JOIN 会给匹配不上的右表补 NULL 行，`WHERE c.xxx = '王老师'` 把 NULL 行全过滤掉，等价于 INNER JOIN。条件要挪到 `ON` 里。
6. `COUNT(*)` 数所有行；`COUNT(score)` 不数 NULL 行，所以可能更小。
7. 还在。ALTER 是 DDL，会隐式提交事务，ROLLBACK 撤不回来（实测）。
8. REPEATABLE READ；快照读靠 MVCC 快照不会幻读，当前读（FOR UPDATE）靠 next-key lock（间隙锁）。
9. DOUBLE 是二进制浮点近似值，累加会有精度误差，对账会差钱；DECIMAL 是精确十进制。
10. 把唯一索引改成 `(name, deleted)`，`deleted BIGINT DEFAULT 0`，删除时 `SET deleted = id`（实测同名可共存）。别用 `deleted_at NULL` 方案，NULL 不参与唯一判断，实测能插两行同名。
11. 因为默认开了 ONLY_FULL_GROUP_BY，`name` 既不在 GROUP BY 里也不是聚合函数，MySQL 不猜"取哪个 name"。
12. 要先扫描并丢弃前 10 万行。优化：记上一页最大 id 用 `WHERE id > ? ORDER BY id LIMIT 10`，或延迟关联先只查主键再回表。

</details>

---

## 附：这篇和你已有材料的关系

- `java/day25/知识点.md`：MySQL 入门（本篇第 0~7 节是它的升级版）
- `java/day28/知识点.md`：隔离级别 + 连接池（本篇第 8 节）
- `java/day35/知识点.md`：约束六件套 + 字符集 + 逻辑删除（本篇第 2、10 节）
- Day36~37：索引进阶（B+ 树细节、EXPLAIN 实战、索引优化）——本篇第 9 节只是结论版
- 本篇所有"🔬 实测"结论都在本机 MySQL 8.4.5 上跑过；换机器/换版本（尤其 5.7）个别行为会变（比如 5.7 没有 CHECK、默认排序规则是 `utf8mb4_general_ci`）。
