-- ============================================================
--  Day37：你的实验本（第 3、5、6 步要写的 SQL / DDL 都放这里）
--  跑法：菜单 3（计时） / 菜单 4（EXPLAIN） / 菜单 5（EXPLAIN ANALYZE）
--  约定：一条语句一个分号；注释单独占一行（-- 开头）
-- ============================================================

-- ── 第 3 步：EXPLAIN 阶梯（题目给的，直接抄过来跑也行）──
-- SELECT * FROM member WHERE id = 1;
-- SELECT m.id, c.province FROM member m JOIN city_dict c ON m.city = c.name WHERE m.id < 50;
-- SELECT * FROM member WHERE city = '郑州';
-- SELECT * FROM member WHERE id BETWEEN 100 AND 200;
-- SELECT COUNT(*) FROM member;
-- SELECT * FROM member WHERE status = 1;

-- ── 第 5 步：ICP 对照（题目给的）──
-- SELECT * FROM member WHERE city = '郑州' AND status = 1;
-- SELECT * FROM member FORCE INDEX (idx_city_age_score) WHERE city = '郑州' AND age > 20 AND score < 10;

-- ── 第 6 步：索引设计（题目给的 DDL，索引名别改）──
-- ALTER TABLE member ADD INDEX idx_city_status_score (city, status, score);
-- ALTER TABLE member ADD INDEX idx_name (name);
-- ALTER TABLE member DROP INDEX idx_city_age;

-- ── 你自己的实验（随便写：想验证的写法、想对比的索引）──
-- ============================================================
--  Day37：你的实验本（修改版，去掉了SQL前的注释）
--  跑法：菜单3(计时) / 菜单4(EXPLAIN) / 菜单5(EXPLAIN ANALYZE)
-- ============================================================

-- ── 【第6步：必须先跑！建/删索引的DDL】（因为后面的查询依赖这些索引）──
ALTER TABLE member ADD INDEX idx_city_status_score (city, status, score);
ALTER TABLE member ADD INDEX idx_name (name);
ALTER TABLE member DROP INDEX idx_city_age;

-- ── 【第3步：EXPLAIN 阶梯（题目给的，去掉注释）】──
SELECT * FROM member WHERE id = 1;
SELECT m.id, c.province FROM member m JOIN city_dict c ON m.city = c.name WHERE m.id < 50;
SELECT * FROM member WHERE city = '郑州';
SELECT * FROM member WHERE id BETWEEN 100 AND 200;
SELECT COUNT(*) FROM member;
SELECT * FROM member WHERE status = 1;

-- ── 【第5步：ICP 对照（题目给的，去掉注释）】──
SELECT * FROM member WHERE city = '郑州' AND status = 1;
SELECT * FROM member FORCE INDEX (idx_city_age_score) WHERE city = '郑州' AND age > 20 AND score < 10;

-- ── 【自定义实验：今天学的「覆盖索引 Using index」验证！】──
-- ✅ 实验1：用idx_name（你建的普通索引）→ 覆盖索引（只查id/name）
SELECT id, name FROM member WHERE name = '张三';
-- ❌ 实验2：用idx_name → 不覆盖（查了*，有name以外的字段）
SELECT * FROM member WHERE name = '张三';
-- ✅ 实验3：用你建的联合索引idx_city_status_score → 覆盖索引（只查联合索引的3列）
SELECT city, status, score FROM member WHERE city = '郑州';
-- ❌ 实验4：用idx_city_status_score → 不覆盖（查了name，不在联合索引里）
SELECT city, status, score, name FROM member WHERE city = '郑州';
