-- 题 3：条件查询（WHERE + ORDER BY + LIMIT）
-- 数据已有 7 人（见下方固定语句），要求**一条 SELECT** 查出：
--   ① 只查「一班」的同学
--   ② 分数 ≥ 80
--   ③ 只要 name 和 score 两列
--   ④ 按分数**从高到低**排序
--   ⑤ 只取**前 2 条**（LIMIT 2）
-- 期望输出（两行）：
--   李四 95
--   张三 88
-- 提示：WHERE 多个条件用 AND；降序是 ORDER BY ... DESC；LIMIT 写在最后
-- ⚠️ 顺序不能乱：SELECT → FROM → WHERE → ORDER BY → LIMIT

CREATE DATABASE IF NOT EXISTS day25 DEFAULT CHARACTER SET utf8mb4;
USE day25;
DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(20) NOT NULL,
  score      INT DEFAULT 0,
  class_name VARCHAR(20)
) DEFAULT CHARSET = utf8mb4;
INSERT INTO student (name, score, class_name) VALUES
('张三', 88, '一班'), ('李四', 95, '一班'), ('王五', 76, '一班'),
('赵六', 60, '二班'), ('钱七', 100, '二班'),
('孙八', 45, '三班'), ('周九', 82, '三班');

-- ===== 你的 SQL 写在这里 =====
SELECT name,score FROM student
WHERE class_name='一班'AND score>=80
ORDER BY score DESC
LIMIT 2;



-- ===========================
