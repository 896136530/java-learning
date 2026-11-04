-- ============================================================
--  Day38 数据：day38 库 + 2 张表 + 8 条学生 + 3 个班
--  用法：双击 运行Day38.bat → 菜单 1，或者
--        E:\mysql\bin\mysql.exe -u root -proot < sql\01_setup.sql
--  ⚠️ 只动 day38 这个库，别碰 day21~day37
-- ============================================================

CREATE DATABASE IF NOT EXISTS day38
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'day38'@'localhost' IDENTIFIED BY 'day38';
GRANT ALL PRIVILEGES ON day38.* TO 'day38'@'localhost';
FLUSH PRIVILEGES;

USE day38;

DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS class_info;

CREATE TABLE class_info (
  id      INT         NOT NULL AUTO_INCREMENT,
  name    VARCHAR(20) NOT NULL,
  teacher VARCHAR(20) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE student (
  id          INT          NOT NULL AUTO_INCREMENT,
  name        VARCHAR(20)  NOT NULL,
  score       INT          NOT NULL DEFAULT 0,
  cls_id      INT          NULL,
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_score (score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO class_info (name, teacher) VALUES
('一班', '王老师'), ('二班', '李老师'), ('三班', '赵老师');

INSERT INTO student (name, score, cls_id, create_time) VALUES
('张三',  88, 1, '2026-09-01 08:00:00'),
('李四',  95, 1, '2026-09-01 08:05:00'),
('王五',  76, 2, '2026-09-02 09:00:00'),
('赵六',  60, 2, '2026-09-02 09:10:00'),
('钱七', 100, 3, '2026-09-03 10:00:00'),
('孙八',  45, 3, '2026-09-03 10:20:00'),
('周九',  82, 1, '2026-09-04 11:00:00'),
('吴十',  90, 2, '2026-09-04 11:30:00');

-- 自检：8 行学生、3 个班
SELECT COUNT(*) AS 学生数 FROM student;
SELECT cls_id, COUNT(*) AS 人数 FROM student GROUP BY cls_id;
SELECT * FROM student;
SELECT * FROM class_info;