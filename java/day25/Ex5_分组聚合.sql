-- 题 5：分组统计（GROUP BY + 聚合函数）
-- 数据已有 7 人、三个班（见下方固定语句）。要求**一条 SELECT**：
--   按班级分组，输出每个班的：班级名、人数、平均分（保留 2 位小数）、最高分
--   并且按**平均分从高到低**排序
-- 期望输出（三行）：
--   一班 3 人 平均 86.33 最高 95
--   二班 2 人 平均 80.00 最高 100
--   三班 2 人 平均 63.50 最高 82
-- 提示：
--   · 人数 COUNT(*)、平均分 AVG(score)、最高分 MAX(score)
--   · 平均值保留 2 位：ROUND(AVG(score), 2)
--   · 起中文别名用 AS，例如 COUNT(*) AS 人数
--   · 排序想用平均值：ORDER BY AVG(score) DESC
-- ⚠️ GROUP BY 后面必须写分组的列；SELECT 里非聚合的列都要出现在 GROUP BY 里

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
SELECT class_name AS 班级,
COUNT(*)AS 人数,
ROUND(AVG(score),2)AS 平均分,
MAX(score)AS 最高分
FROM student
GROUP BY class_name
ORDER BY AVG(score) DESC;


-- ===========================
