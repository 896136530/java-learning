-- ============================================================
--  Day36：你的实验本（第 4~6 步的 SQL 都写这里）
--  跑法：菜单 3（计时） / 菜单 4（EXPLAIN）
--  约定：一条语句一个分号；注释单独占一行（-- 开头）
-- ============================================================

-- ── 第 4 步：最左前缀（索引 idx_city_age_status = city, age, status）──
SELECT * FROM member WHERE city = '郑州';
SELECT * FROM member WHERE city = '郑州' AND age = 25;
SELECT * FROM member WHERE city = '郑州' AND status = 1;
SELECT * FROM member WHERE age = 25;
SELECT * FROM member WHERE status = 1 AND city = '郑州';
SELECT * FROM member WHERE age = 25 AND status = 1;
SELECT * FROM member WHERE city = '郑州' AND age > 30 AND status = 1;

-- ── 第 5 步：索引失效 5 种写法 + 自选 1 条 ──
SELECT * FROM member WHERE LEFT(phone, 3) = '138';
SELECT * FROM member WHERE phone = 13800000001;
SELECT * FROM member WHERE phone LIKE '%1234567';
SELECT * FROM member WHERE phone = '13800000001' OR name = '用户7';
SELECT * FROM member WHERE city = '郑州' AND age > 30 AND status = 1;

-- 自选：OR 的一边（age）用不到最左列 city，整条退化成全表扫
SELECT * FROM member WHERE city = '郑州' OR age = 25;

-- ── 第 6 步：给 3 条业务 SQL 开索引 ──
SELECT * FROM member WHERE phone = '13800000001';
SELECT * FROM member WHERE city = '郑州' AND age BETWEEN 20 AND 30 AND status = 1;
SELECT id, phone, name FROM member WHERE name LIKE '用户99%';

-- 第 6 步的 DDL（② 和 ③ 用，已执行过；重跑前先菜单 1 重置，否则报 Duplicate key name）
-- ALTER TABLE member ADD INDEX idx_city_status_age (city, status, age);
-- ALTER TABLE member ADD INDEX idx_name (name);
-- 注：idx_city_status_age 只是拿来验证 ② 的 key_len（结论：和现有索引一样 87、零收益），验证完已 DROP。

-- 本机实测日志（2026-10-30 重跑复核，可复查）：
--   target\bench-before.log    基线：161/178/179 ms、145/171/170 ms
--   target\bench-after.log     加 idx_phone 后：0.34/0.63/0.66 ms、0.30/0.48/0.51 ms
--   target\bench-lookup.log    回表：13.50 ms（PRIMARY）vs 69.30 ms（idx_phone）
--   target\explain-before.log  第 4~6 步 EXPLAIN（加 idx_name 之前）
--   target\explain-after.log   第 4~6 步 EXPLAIN（加 idx_name 之后）