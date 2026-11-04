-- ============================================================
--  第 4 步：排序与深分页
--  跑法：java -jar target\day37-bench.jar sql\04_排序分页.sql
--        （数字要在"建 idx_created 之前"和"之后"各跑一次）
--
--  A 深分页 · 老写法：LIMIT 150000, 20
--  B 深分页 · 延迟关联：先用覆盖索引跳过 15 万行，只回表最后 20 行
--  C 深分页 · 游标法：用"上一页最后一条的时间"当起点（真项目里这个值由前端带回来）
--     这里用 NOW() - 274 天 模拟"翻到第 15 万行附近"，你不用改
-- ============================================================

-- 第 0 条：顺手确认排序真有用（前 20 条 + 索引下有没有 filesort）
SELECT id, created_at FROM member ORDER BY created_at DESC LIMIT 20;

-- A 老写法
SELECT * FROM member ORDER BY created_at DESC LIMIT 150000, 20;

-- B 延迟关联
SELECT m.* FROM member m
JOIN (SELECT id FROM member ORDER BY created_at DESC LIMIT 150000, 20) t ON m.id = t.id;

-- C 游标法
SELECT * FROM member
WHERE created_at < NOW() - INTERVAL 274 DAY
ORDER BY created_at DESC LIMIT 20;