-- ═══════════════════════════════════════════════════════════════════════════════
--  Day 35 · schema.sql —— 商品/订单/审计 三张表的**目标表结构**（MySQL 8 版）
-- ═══════════════════════════════════════════════════════════════════════════════
--
--  【这个文件是什么？】
--    这就是 TODO③（第 5 步）要交付的东西：**建表语句不再写在 Java 里，而是放这儿**。
--    然后由 SpringBoot 的 `spring.sql.init` 在启动时自动执行（见 application.properties ⑤）。
--
--  【怎么用？—— 两种方式，都要会】
--   方式 A（推荐，先看到原文再自动化）：用 mysql 命令行执行一次
--       E:\mysql\bin\mysql.exe -u root -proot day35 < java\day35\src\main\resources\db\schema.sql
--     然后 `SHOW CREATE TABLE sku\G` 看建表原文（证明约束真的进去了）。
--   方式 B（TODO③ 的目标）：让 SpringBoot 在启动时自动跑
--       spring.sql.init.mode=always
--       spring.sql.init.schema-locations=classpath:db/schema.sql
--
--  ⚠️ 本文件**只负责建表，不负责插种子数据**。
--     种子数据仍在 `db/DataInitializer.java` 里（它还要被 /db/reset 反复调用）。
--     所以 TODO③ 是"DDL 搬家"，不是"初始化整体搬家" —— 这个分工要分清楚。
--
--  ⚠️ 本文件**不含 CREATE DATABASE**：建库那一步是第 1 步手动做的（建库语句在题目里）。
--     原因：`USE day35` 之类的语句在 Spring 的 SQL 初始化里会有额外限制，分开更清楚。
--
--  ═══════════════════════════════════════════════════════════════════════════════
--  ⭐ 逐条对照"要学的 6 种约束"，一个都不少（这就是第 2 步的验收表）
--  ═══════════════════════════════════════════════════════════════════════════════
--    ① PRIMARY KEY       → id BIGINT AUTO_INCREMENT PRIMARY KEY
--    ② NOT NULL          → code / title / price / stock / is_deleted / created_at ...
--    ③ DEFAULT           → price 0.00、stock 0、is_deleted 0、created_at CURRENT_TIMESTAMP
--    ④ UNIQUE（唯一约束） → uk_sku_code (code)          —— 在 MySQL 里"唯一约束"就是"唯一索引"
--    ⑤ FOREIGN KEY        → fk_orders_sku (orders.sku_id → sku.id)
--    ⑥ CHECK             → ck_sku_stock / ck_orders_count / ck_orders_amount
--    ⭐ 外加：**联合唯一索引** uk_audit_action_detail (action, detail)
--    ⭐ 外加：字符集一律 utf8mb4、金额一律 DECIMAL(10,2)、逻辑删除 is_deleted
--
--  ═══════════════════════════════════════════════════════════════════════════════
--  ⚠️⚠️ 真机实测挖到的 4 个坑（都是本机 MySQL 8.4.5 上跑出来的，不是抄的）
--  ═══════════════════════════════════════════════════════════════════════════════
--   坑 1：**MySQL 不允许 CHECK 引用自增列**
--         `CONSTRAINT ck_x CHECK (id >= 0)` 建表直接失败：
--           ERROR 3818 (HY000): Check constraint 'ck_x' cannot refer to an auto-increment column.
--         → 要约束数值范围，就约束**业务列**（price / stock / count / amount），别碰 id。
--
--   坑 2：**约束名（含 CHECK / UNIQUE / FK 名）在整个库（schema）里必须唯一**，不是每张表唯一！
--         我第一版两张表都写了同名 `CONSTRAINT ck_sku_stock`，第二张表直接失败：
--           ERROR 3822 (HY000): Duplicate check constraint name 'ck_sku_stock'.
--         → 所以这里每条约束都起了**全局唯一**的名字（ck_orders_count / ck_sku_stock …）。
--
--   坑 3：**CHECK 在 MySQL 5.7 上会被静默忽略**（解析了但从来不生效），MySQL **8.0.16 起才真正生效**。
--         本机 8.4.5 是生效的（实测：插 stock=501 → ERROR 3819 Check constraint 'ck_sku_stock' is violated.）。
--         📌 面试点："你项目里的 CHECK 生效吗？"——先问对方 MySQL 版本。
--
--   坑 4：`count` 是**内置函数名**（`COUNT(*)`），⚠️ 但它**不是保留字** ——
--         真机实测（MySQL 8.4.5）：`CREATE TABLE t (id INT PRIMARY KEY, count INT)` **建表成功**，
--         `SELECT count FROM t` 也能跑。真正裸用会炸的是 `order` / `desc` / `key` / `group` 那类：
--         `CREATE TABLE t (id INT PRIMARY KEY, order INT)` → `ERROR 1064 (42000) ... near 'order INT'`。
--         ⭐ 不过列名叫 `count` 仍然**推荐加反引号** `count` —— 它和 `COUNT()` 同名，
--            在复杂表达式里容易歧义。**统一加反引号是好习惯**（比背"哪个词是保留字"省心）。
--         实测：MySQL 和 H2 都接受反引号，所以 DDL / SQL 里统一加反引号最稳。
--         📌 更稳的做法是**别拿容易撞的词当列名**（叫 quantity 就没事）—— 这是坏味道。
--
--  ═══════════════════════════════════════════════════════════════════════════════
--  ⚠️ 关于 DROP TABLE：**只能 DROP 自己这个库（day35）里的表**
--  ═══════════════════════════════════════════════════════════════════════════════
--    下面的 DROP TABLE IF EXISTS 是"可重复执行"用的（重跑一次不会报"表已存在"）。
--    ⚠️⚠️ 千万不要把库名写成别的、也不要在别的库里执行本文件 ——
--       你机器上 day21 / day25 / day26 / day27 / day28 / day29 里都有真数据。
--    ⚠️ 顺序有讲究：**先删子表（orders）再删父表（sku）**，
--       否则外键会拦住（`Cannot drop table 'sku' referenced by a foreign key constraint`）。
-- ═══════════════════════════════════════════════════════════════════════════════

DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS ch_demo;
DROP TABLE IF EXISTS sku;

-- ───────────────────────────────────────────────────────────────────────────────
-- ① sku（商品表）—— 约束最全的一张，重点看 6 处标记
-- ───────────────────────────────────────────────────────────────────────────────
CREATE TABLE sku (
  id         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  code       VARCHAR(20)   NOT NULL                COMMENT '商品编码，业务主键',
  title      VARCHAR(100)  NOT NULL                COMMENT '书名',
  price      DECIMAL(10,2) NOT NULL DEFAULT 0.00   COMMENT '价格：金额绝不用 float/double',
  stock      INT           NOT NULL DEFAULT 0      COMMENT '库存',
  is_deleted TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除：0=还在，1=已删',
  created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（数据库自己填）',
  PRIMARY KEY (id),                                              -- ① 主键
  CONSTRAINT uk_sku_code UNIQUE (code),                          -- ④ 唯一约束 = 唯一索引
  CONSTRAINT ck_sku_price CHECK (price >= 0),                    -- ⑥ 价格不能为负（和 SkuForm 的 @Min(0) 同一条规则）
  CONSTRAINT ck_sku_stock CHECK (stock >= 0 AND stock <= 500)     -- ⑥ 检查约束（和 @MaxStock(500) 同一条规则）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

-- ⭐ **联合唯一索引**：同一组 (编码, 删除标记) 只能出现一次
--    它和 uk_sku_code 的区别（第 2 步思考题）：
--      · uk_sku_code       (code)             → 一个编码**全局**只能有一条（最严格）
--      · uk_sku_code_alive (code, is_deleted) → 允许"删掉的"和"活着的"各留一条
--    两条同时存在时，严格的那条说了算 —— 这就是"逻辑删除 + 唯一索引"最经典的坑：
--    **删掉 SKU-0003 之后，再新增同编码，仍然会被 uk_sku_code 拦住。**
CREATE UNIQUE INDEX uk_sku_code_alive ON sku (code, is_deleted);

-- ───────────────────────────────────────────────────────────────────────────────
-- ② orders（订单表）—— 演示 ⑤ 外键 + 金额类型
-- ───────────────────────────────────────────────────────────────────────────────
CREATE TABLE orders (
  id         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  sku_id     BIGINT        NOT NULL                COMMENT '买的哪个 SKU（外键指向 sku.id）',
  sku_code   VARCHAR(20)   NOT NULL                COMMENT '下单那一刻的编码快照',
  title      VARCHAR(100)  NOT NULL                COMMENT '下单那一刻的书名快照',
  `count`    INT           NOT NULL                COMMENT '数量（与 COUNT() 同名，统一加反引号）',
  amount     DECIMAL(10,2) NOT NULL                COMMENT '金额 = price * count',
  created_at DATETIME      NOT NULL                COMMENT '下单时间',
  PRIMARY KEY (id),                                                       -- ① 主键
  CONSTRAINT ck_orders_count  CHECK (`count` > 0),                        -- ⑥ 数量必须 > 0
  CONSTRAINT ck_orders_amount CHECK (amount >= 0),                        -- ⑥ 金额不能为负
  CONSTRAINT fk_orders_sku FOREIGN KEY (sku_id) REFERENCES sku (id)        -- ⑤ 外键
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- ───────────────────────────────────────────────────────────────────────────────
-- ③ audit_log（审计日志表）—— 演示**联合唯一索引**的现实用法
-- ───────────────────────────────────────────────────────────────────────────────
CREATE TABLE audit_log (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键（自增）',
  action     VARCHAR(50)  NOT NULL                COMMENT '动作，如 PLACE / DAILY_CLOSE',
  detail     VARCHAR(200) NOT NULL                COMMENT '详情',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  PRIMARY KEY (id),
  CONSTRAINT uk_audit_action_detail UNIQUE (action, detail)        -- ⭐ 联合唯一索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='审计日志表';

-- ⭐⭐ 为什么审计表要"联合唯一"？——**记账不能重复记**
--    规则：同一个动作 + 同一段详情，只允许存在一条。
--    现实中就是"每天的日终对账只记一次"（detail 里带日期，第二天日期变了就不会撞）。
--    ⭐ 这正是计划表里那个例子 `UNIQUE KEY uk_user_date (user_id, day)` 的同款结构。
--    ⚠️ 联合索引里**列的顺序**有意义：(action, detail) 和 (detail, action) 是两个不同的索引。

-- ───────────────────────────────────────────────────────────────────────────────
-- ④ ch_demo（字符集对照表）—— 只为教学存在，看完可以删
-- ───────────────────────────────────────────────────────────────────────────────
--  ⭐ 它证明一件事：**MySQL 的 `utf8` 是假的 UTF-8**（其实是 utf8mb3，每字符最多 3 字节），
--     而 emoji（😀 = 4 字节）塞不进去。真机实测 MySQL 8.4.5：
--        INSERT ... 到 utf8_col  → ERROR 1366 (HY000): Incorrect string value:
--                                  '\xF0\x9F\x98\x80' for column 'utf8_col' at row 1
--        INSERT ... 到 mb4_col   → 成功；读回来 HEX=F09F9880、LENGTH=4、CHAR_LENGTH=1
--  ⭐ 结论：**建库建表一律 utf8mb4，永远不要写 utf8。**
--  ⚠️ 注意：这张表**只有 MySQL 建得出来** —— H2 的 CREATE TABLE 不支持列级 `CHARACTER SET`
--     （真机实测 H2 2.2.224 报语法错）。所以交付版跑 H2 时它不存在，这是**预期**的。
CREATE TABLE ch_demo (
  id        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  utf8_col  VARCHAR(20) CHARACTER SET utf8    NOT NULL COMMENT '假的 utf8：最多 3 字节，存不了 emoji',
  mb4_col   VARCHAR(20) CHARACTER SET utf8mb4 NOT NULL COMMENT '真 utf8mb4：4 字节，emoji 随便存',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字符集对照表（教学用）';

-- ═══════════════════════════════════════════════════════════════════════════════
--  ⭐ 附：/db/reset 在 MySQL 上要用什么？（DataInitializer.reset() 的对应写法）
-- ═══════════════════════════════════════════════════════════════════════════════
--  交付版（H2）用的是：
--      TRUNCATE TABLE orders RESTART IDENTITY            ← `RESTART IDENTITY` 是 H2 方言
--  MySQL 上要改成：
--      TRUNCATE TABLE orders;                            ← MySQL 的 TRUNCATE **自带重置自增**
--      TRUNCATE TABLE audit_log;
--      TRUNCATE TABLE sku;
--      TRUNCATE TABLE ch_demo;
--  ⭐ 三点必须知道：
--   ① MySQL **没有** `RESTART IDENTITY` 这个语法，写了直接报语法错（这就是"方言差异"）
--   ② MySQL 的 TRUNCATE 之后自增回到 1（和 H2 的 RESTART IDENTITY 效果一样）
--   ③ **先 truncate orders 再 truncate sku**（外键顺序）；
--      如果顺序实在绕不开，可以临时 `SET FOREIGN_KEY_CHECKS=0` … `SET FOREIGN_KEY_CHECKS=1`
--      —— 但**别在真实环境养成这个习惯**，它会让外键检查整个失效。
-- ═══════════════════════════════════════════════════════════════════════════════