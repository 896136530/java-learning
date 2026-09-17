-- 题 1：建库建表
-- 要求（三件事）：
--   ① 如果 day25 库已存在就先删掉，然后创建 day25（字符集 utf8mb4）
--   ② 切到 day25（USE）
--   ③ 创建 student 表，四个字段照抄：
--        id         INT          主键（PRIMARY KEY）、自增（AUTO_INCREMENT）
--        name       VARCHAR(20)  不能为空（NOT NULL）
--        score      INT          默认 0（DEFAULT 0）
--        class_name VARCHAR(20)
--      表尾加：DEFAULT CHARSET = utf8mb4
-- 期望：
--   SHOW DATABASES 里出现 day25；SHOW TABLES 里出现 student；
--   DESC student 显示 4 个字段，id 的 Key = PRI、Extra = auto_increment
-- 提示：DROP DATABASE IF EXISTS / CREATE DATABASE / USE / CREATE TABLE

-- ===== 你的 SQL 写在这里 =====
DROP DATABASE IF EXISTS day25;
CREATE DATABASE  day25 DEFAULT CHARACTER SET utf8mb4;
USE day25;
DROP TABLE IF EXISTS student;
CREATE TABLE student(
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    name VARCHAR(20) NOT NULL COMMENT '姓名',
    score INT DEFAULT 0 COMMENT '成绩',
    class_name VARCHAR(20) COMMENT '班级'
)DEFAULT CHARSET=utf8mb4;


-- ===========================

-- ↓↓↓ 下面是验证语句（别改）↓↓↓
SHOW DATABASES LIKE 'day25';
SHOW TABLES;
DESC student;
