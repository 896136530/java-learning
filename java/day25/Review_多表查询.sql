-- 🎯 复习：多表查询（JOIN）——把 Day24 的"两张表"用数据库的方式再做一遍
-- 现在有两张表（下方固定语句已建好并塞好数据）：
--   student(id, name, score, class_name)      —— 7 个学生
--   class_info(class_name, teacher, room)     —— 3 个班，班级名是主键
-- 要求写**两条** SELECT：
--   ① 查每个学生的：姓名、分数、班级、班主任（student 和 class_info 连接），按分数**降序**
--   ② 查每个班的：班级、人数、班主任，按**人数降序**（人数相同按班级名升序兜底）
-- 期望输出（① 七行 / ② 三行）：
--   钱七 100 二班 李老师 ...（略）
--   一班 3 王老师 / 三班 2 张老师 / 二班 2 李老师
-- 提示：
--   · 连表：FROM student s JOIN class_info c ON s.class_name = c.class_name
--   · 两条以上的排序条件用逗号：ORDER BY COUNT(*) DESC, c.class_name
--   · 别名：给表起短名（s / c）后，字段写 s.name、c.teacher
-- ⚠️ 别忘了 ON 后面的连接条件，否则会变成"笛卡尔积"（7×3 = 21 行）

CREATE DATABASE IF NOT EXISTS day25 DEFAULT CHARACTER SET utf8mb4;
USE day25;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS class_info;
CREATE TABLE student (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(20) NOT NULL,
  score      INT DEFAULT 0,
  class_name VARCHAR(20)
) DEFAULT CHARSET = utf8mb4;
CREATE TABLE class_info (
  class_name VARCHAR(20) PRIMARY KEY,
  teacher    VARCHAR(20),
  room       VARCHAR(20)
) DEFAULT CHARSET = utf8mb4;
INSERT INTO student (name, score, class_name) VALUES
('张三', 88, '一班'), ('李四', 95, '一班'), ('王五', 76, '一班'),
('赵六', 60, '二班'), ('钱七', 100, '二班'),
('孙八', 45, '三班'), ('周九', 82, '三班');
INSERT INTO class_info VALUES
('一班', '王老师', 'A101'),
('二班', '李老师', 'A102'),
('三班', '张老师', 'A103');

-- ===== 你的 SQL 写在这里 =====



-- ===========================
