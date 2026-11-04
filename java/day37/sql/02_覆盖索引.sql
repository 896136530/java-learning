-- ============================================================
--  第 2 步：覆盖索引 —— 同一个文件，改造前后各跑一次
--  改造前：表上只有 PRIMARY + idx_city_age(city, age)
--  改造后：多一个 idx_city_age_phone(city, age, phone)
--
--  跑法：java -jar target\day37-bench.jar sql\02_覆盖索引.sql
--        java -jar target\day37-bench.jar --explain sql\02_覆盖索引.sql
-- ============================================================

-- ① 只要 phone 一列，但要 2 万行：索引里没有 phone → 每一行都得回表
SELECT phone FROM member WHERE city = '郑州';

-- ② 条件更窄（466 行），同样要回表
SELECT phone FROM member WHERE city = '郑州' AND age = 25;

-- ③ 数行数：这条**不用回表**，任何二级索引都能数（看 Extra）
SELECT COUNT(*) FROM member;

-- ④ 对照实验：要整行（SELECT *），就算索引里有 phone 也覆盖不了
SELECT * FROM member WHERE city = '郑州' AND age = 25;