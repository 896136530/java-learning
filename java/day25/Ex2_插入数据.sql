-- 题 2：插入数据（INSERT）
-- 表已经建好了（下面那段"固定的建表语句"，别改）
-- 要求：用**一条** INSERT 语句一次插入三条记录
--   张三 88 分 一班 / 李四 95 分 一班 / 王五 76 分 一班
-- 期望输出：
--   id=1 张三 88 一班
--   id=2 李四 95 一班
--   id=3 王五 76 一班
--   人数 = 3
-- 提示：INSERT INTO student (name, score, class_name) VALUES (...), (...), (...);
-- ⚠️ 字符串用**单引号**，别用双引号（双引号在 MySQL 里默认是列名）

CREATE DATABASE IF NOT EXISTS day25 DEFAULT CHARACTER SET utf8mb4;
USE day25;
DROP TABLE IF EXISTS student;
CREATE TABLE student (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(20) NOT NULL,
  score      INT DEFAULT 0,
  class_name VARCHAR(20)
) DEFAULT CHARSET = utf8mb4;

-- ===== 你的 SQL 写在这里 =====
INSERT INTO student (name, score, class_name)
VALUES ('张三', 88, '一班'),
       ('李四', 95, '一班'),
       ('王五', 76, '一班');


-- ===========================

-- ↓↓↓ 下面是验证语句（别改）↓↓↓
SELECT id, name, score, class_name FROM student ORDER BY id;
SELECT COUNT(*) AS 人数 FROM student;
