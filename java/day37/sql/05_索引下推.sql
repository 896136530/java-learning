-- ============================================================
--  第 5 步：索引下推（ICP）
--  跑法：java -jar target\day37-bench.jar sql\05_索引下推.sql
--        java -jar target\day37-bench.jar --explain sql\05_索引下推.sql
--
--  跑之前先建索引（在 mysql 里执行）：
--    ALTER TABLE member ADD INDEX idx_city_age_score (city, age, score);
--    ANALYZE TABLE member;
-- ============================================================

-- ⓪ 先看反例：status 不在任何索引里 → ICP 想推也推不了
SELECT * FROM member WHERE city = '郑州' AND status = 1;

-- A 交给优化器自己挑索引
SELECT * FROM member WHERE city = '郑州' AND age > 20 AND score < 10;

-- B 强制走带 score 的索引：score<10 能被推到引擎层过滤
SELECT * FROM member FORCE INDEX (idx_city_age_score)
WHERE city = '郑州' AND age > 20 AND score < 10;

-- C 同一个索引，把 ICP 关掉 —— 只能回表之后再由 Server 层过滤
SET SESSION optimizer_switch = 'index_condition_pushdown=off';
SELECT * FROM member FORCE INDEX (idx_city_age_score)
WHERE city = '郑州' AND age > 20 AND score < 10;
SET SESSION optimizer_switch = 'index_condition_pushdown=on';