-- ============================================================
--  第 6 步：三条业务 SQL 开索引 + 清理冗余
--  跑法：改造前后各跑一次：
--        java -jar target\day37-bench.jar sql\06_索引设计.sql
--        java -jar target\day37-bench.jar --explain sql\06_索引设计.sql
--
--  你自己要写的 DDL（写在 90_我的实验.sql 里，索引名必须照题目）：
--    ALTER TABLE member ADD INDEX idx_city_status_score (city, status, score);
--    ALTER TABLE member ADD INDEX idx_name (name);
-- ============================================================

-- ① 列表页：某城市、在用的会员，按积分从高到低取前 20
SELECT id, name, phone, score FROM member
WHERE city = '郑州' AND status = 1
ORDER BY score DESC LIMIT 20;

-- ② 搜索页：按姓名前缀模糊匹配
SELECT id, name FROM member WHERE name LIKE '用户199%';

-- ③ 看现有索引（每一行 = 一个索引的"第几列"）
SELECT INDEX_NAME, SEQ_IN_INDEX, COLUMN_NAME, SUB_PART, NON_UNIQUE
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'day37' AND TABLE_NAME = 'member'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- ④ 前缀区分度：前缀几位才能"基本不重复"？（本表名字最长 8 个字符）
SELECT COUNT(DISTINCT LEFT(name, 4)) AS 前4位,
       COUNT(DISTINCT LEFT(name, 6)) AS 前6位,
       COUNT(DISTINCT LEFT(name, 8)) AS 前8位,
       COUNT(DISTINCT name)          AS 全名
FROM member;

-- ⑤ 整张表所有索引一共占多少字节
SELECT INDEX_LENGTH FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'day37' AND TABLE_NAME = 'member';