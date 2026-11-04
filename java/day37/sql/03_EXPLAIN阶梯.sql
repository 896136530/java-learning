-- ============================================================
--  第 3 步：EXPLAIN 阶梯 —— 六种 type 一次打全
--  跑法：java -jar target\day37-bench.jar --explain sql\03_EXPLAIN阶梯.sql
--        java -jar target\day37-bench.jar --analyze sql\03_EXPLAIN阶梯.sql   （看真实行数）
-- ============================================================

-- ① const：主键等值，最多 1 行
SELECT * FROM member WHERE id = 1;

-- ② eq_ref：联表时"对方表走唯一索引"，每行只匹配 1 条
SELECT m.id, c.province FROM member m JOIN city_dict c ON m.city = c.name WHERE m.id < 50;

-- ③ ref：普通索引等值
SELECT * FROM member WHERE city = '郑州';

-- ④ range：索引上的范围
SELECT * FROM member WHERE id BETWEEN 100 AND 200;

-- ⑤ index：扫整棵索引树（这里数行数，不用回表）
SELECT COUNT(*) FROM member;

-- ⑥ ALL：全表扫（status 上没有索引）
SELECT * FROM member WHERE status = 1;

-- ⑦ 估算 vs 真实：看一眼 filtered 怎么"猜"的
SELECT * FROM member WHERE city = '郑州' AND status = 1;