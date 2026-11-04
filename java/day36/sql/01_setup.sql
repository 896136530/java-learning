-- ============================================================
--  Day36 实验数据：20 万行 member 表（一次跑完约 10~20 秒）
--  用法：双击 运行Day36.bat 菜单 1，或者
--        E:\mysql\bin\mysql.exe -u root -proot < sql\01_setup.sql
--  ⚠️ 只动 day36 这个库，别碰 day21 / day25 / day26 / day27 / day28 / day29 / day35
-- ============================================================

CREATE DATABASE IF NOT EXISTS day36
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- 专用账号（最小权限：只给 day36.*）
CREATE USER IF NOT EXISTS 'day36'@'localhost' IDENTIFIED BY 'day36';
GRANT ALL PRIVILEGES ON day36.* TO 'day36'@'localhost';
FLUSH PRIVILEGES;

USE day36;

DROP TABLE IF EXISTS member;

-- 注意：除了主键，故意一个索引都不建（这是今天的"基线状态"）
CREATE TABLE member (
  id         BIGINT      NOT NULL AUTO_INCREMENT,
  phone      VARCHAR(20) NOT NULL,   -- 手机号：区分度极高（20 万个手机号）
  name       VARCHAR(30) NOT NULL,
  city       VARCHAR(20) NOT NULL,   -- 只有 10 个城市：区分度低
  age        INT         NOT NULL,   -- 18~60
  status     TINYINT     NOT NULL DEFAULT 1,  -- 只有 0/1：区分度极低
  created_at DATETIME    NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 递归 CTE 造数据：默认递归上限 1000，必须先放大
SET SESSION cte_max_recursion_depth = 300000;

INSERT INTO member (phone, name, city, age, status, created_at)
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
       IF(n % 7 = 0, 0, 1),                                  -- 约 1/7 停用（用 7 而不是 5，避免和 city 的 n%10 撞成"同生同死"）
       NOW() - INTERVAL (n % 365) DAY
FROM seq;

-- 自检：行数必须是 200000，索引只应有 PRIMARY
SELECT COUNT(*) AS 行数 FROM member;
SHOW INDEX FROM member;
