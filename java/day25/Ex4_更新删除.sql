-- 题 4：更新与删除（UPDATE / DELETE）
-- 数据已有 7 人（见下方固定语句）。要求写**两条**语句：
--   ① UPDATE：给「一班」所有同学每人 +5 分
--   ② DELETE：把所有分数 < 60 的同学删掉
-- 期望输出（验证语句会打印）：
--   一班三人变成 93 / 100 / 81；孙八（45 分）被删掉；剩余 6 人
-- 提示：
--   · UPDATE student SET score = score + 5 WHERE class_name = '一班';
--   · DELETE FROM student WHERE score < 60;
-- ⚠️⚠️ 最危险的坑：**WHERE 忘了写** = 全班加分 / 整张表被清空
--    写 UPDATE / DELETE 前先默念一遍："我的 WHERE 在哪？"

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
UPDATE  student
SET score=score+5
WHERE class_name='一班';
DELETE FROM student
WHERE score<60;


-- ===========================

-- ↓↓↓ 下面是验证语句（别改）↓↓↓
SELECT id, name, score, class_name FROM student ORDER BY id;
SELECT COUNT(*) AS 剩余人数 FROM student;
