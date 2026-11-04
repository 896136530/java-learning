-- ============================================================
--  Day37 实验数据：20 万行 member 表 + 10 行 city_dict（一次跑完约 15~25 秒）
--  用法：双击 运行Day37.bat 菜单 1，或者
--        E:\mysql\bin\mysql.exe -u root -proot < sql\01_setup.sql
--  ⚠️ 只动 day37 这个库，别碰 day21 / day25 / day26 / day27 / day28 / day29 / day35 / day36
-- ============================================================

CREATE DATABASE IF NOT EXISTS day37
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- 专用账号（最小权限：只给 day37.*）
CREATE USER IF NOT EXISTS 'day37'@'localhost' IDENTIFIED BY 'day37';
GRANT ALL PRIVILEGES ON day37.* TO 'day37'@'localhost';
FLUSH PRIVILEGES;

USE day37;

DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS city_dict;

-- ── 小字典表：给第 3 步的 eq_ref（走唯一索引的联表）用 ──
CREATE TABLE city_dict (
  id       INT         NOT NULL AUTO_INCREMENT,
  name     VARCHAR(20) NOT NULL,
  province VARCHAR(20) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO city_dict (name, province) VALUES
('郑州','河南'), ('洛阳','河南'), ('开封','河南'), ('新乡','河南'), ('南阳','河南'),
('安阳','河南'), ('许昌','河南'), ('周口','河南'), ('商丘','河南'), ('信阳','河南');

-- ── 主表：20 万行 ──
-- 起步只给一个联合索引 idx_city_age（这是今天的"已有索引"，第 2 步要把它加宽）
CREATE TABLE member (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  phone      VARCHAR(20) NOT NULL,   -- 手机号：20 万个不同值（区分度极高）
  name       VARCHAR(30) NOT NULL,   -- 用户123456
  city       VARCHAR(20) NOT NULL,   -- 10 个城市（区分度低）
  age        INT         NOT NULL,   -- 18~60
  status     TINYINT     NOT NULL DEFAULT 1, -- 0/1（区分度极低，1/7 是 0）
  score      INT         NOT NULL,   -- 积分 0~999（用来做排序和深分页）
  created_at DATETIME    NOT NULL,   -- 过去一年内，20 万个互不相同的时间
  PRIMARY KEY (id),
  KEY idx_city_age (city, age)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 递归 CTE 造数据：默认递归上限 1000，必须先放大
SET SESSION cte_max_recursion_depth = 300000;

INSERT INTO member (phone, name, city, age, status, score, created_at)
WITH RECURSIVE seq(n) AS (
  SELECT 1
  UNION ALL
  SELECT n + 1 FROM seq WHERE n < 200000
)
SELECT CONCAT('138', LPAD(n, 8, '0')),                       -- 13800000001 ~ 13800200000
       CONCAT('用户', n),
       ELT(1 + (n % 10), '郑州', '洛阳', '开封', '新乡', '南阳',
                         '安阳', '许昌', '周口', '商丘', '信阳'),
       18 + (n % 43),                                        -- 18 ~ 60
       IF(n % 7 = 0, 0, 1),                                  -- 1/7 停用（7 与 10、43 都互质）
       (n * 37) % 1000,                                      -- 0~999 的"伪随机"积分
       NOW() - INTERVAL ((n * 13) % 525600) MINUTE           -- 一年内、互不相同的时间
FROM seq;

ANALYZE TABLE member;

-- ── 打开慢查询日志（第 4 步要用；只对本次 MySQL 运行有效，重启就恢复默认）──
-- 阈值 0.1 秒：超过 100 ms 的 SQL 会被记下来
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 0.1;
SET GLOBAL log_output = 'TABLE';          -- 记到 mysql.slow_log 表里，方便直接查
GRANT SELECT ON mysql.slow_log TO 'day37'@'localhost';   -- 只给读权限
FLUSH PRIVILEGES;

-- 自检：行数必须是 200000；member 上只应有 PRIMARY + idx_city_age
SELECT COUNT(*) AS 行数 FROM member;
SHOW INDEX FROM member;
SHOW TABLE STATUS LIKE 'member'\G