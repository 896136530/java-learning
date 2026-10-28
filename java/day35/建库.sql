-- ============================================================
--  Day35 第 1 步：建库 + 建专用用户（题目 5 分那一步）
--  用法：双击同目录的「双击建库.bat」，或者在命令行执行：
--      E:\mysql\bin\mysql.exe -u root -proot < 建库.sql
--  ⚠️ 这个文件只碰 day35 这一个库，绝不碰 day21/day25~day29
-- ============================================================

-- ① 建库：字符集必须 utf8mb4（不是 utf8！utf8 存不了 emoji）
CREATE DATABASE IF NOT EXISTS day35
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

-- ② 建专用账号：只给 day35.* 的权限（最小权限，炸也炸不大）
CREATE USER IF NOT EXISTS 'day35'@'localhost' IDENTIFIED BY 'day35';
GRANT ALL PRIVILEGES ON day35.* TO 'day35'@'localhost';
FLUSH PRIVILEGES;

-- ============================================================
--  下面全是「验收查询」——不改数据，输出抄进交作业表
-- ============================================================

-- ③ 版本
SELECT VERSION() AS '版本';

-- ④ 库的字符集：必须有 utf8mb4 和 utf8mb4_0900_ai_ci
SHOW CREATE DATABASE day35;

-- ⑤ 机器上有哪些库：能看到 day21/day25... 那些「别人的库」
SHOW DATABASES;

-- ⑥ 账号权限：应该是 GRANT ALL PRIVILEGES ON `day35`.*
SHOW GRANTS FOR 'day35'@'localhost';
