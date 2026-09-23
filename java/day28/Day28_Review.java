import java.sql.*;
import java.util.*;

public class Day28_Review {
    public static void main(String[] args) throws Exception {
        // 🎯 复习：把 Day27 + Day28 拼成一个"像样"的 DAO（写操作 + 批量事务 + 分页）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            System.out.println("=== 1. 单人报名：吴十 ===");
            System.out.println("插入行数：" + StudentDao.insert(conn, "吴十", 90, "二班"));
            System.out.println("现在人数：" + StudentDao.count(conn));
            System.out.println("新人的 id：" + StudentDao.lastInsertId(conn));

            System.out.println("=== 2. 批量报名（事务）：三个人 ===");
            List<Student> group = Arrays.asList(
                    new Student("郑十一", 55, "三班"),
                    new Student("王十二", 99, "一班"),
                    new Student("冯十三", 70, "二班"));
            System.out.println("批量插入行数：" + StudentDao.addAll(conn, group));
            System.out.println("现在人数：" + StudentDao.count(conn));

            System.out.println("=== 3. 分页：每页 3 个，看第 1、2、3 页 ===");
            for (int page = 1; page <= 3; page++) {
                List<Student> one = StudentDao.page(conn, page, 3);
                System.out.println("第 " + page + " 页（" + one.size() + " 条）：" + one);
            }

            System.out.println("=== 4. 删掉一个人，再分页看总数 ===");
            int id = StudentDao.findId(conn, "冯十三");
            System.out.println("冯十三的 id：" + id + "，删除行数：" + StudentDao.deleteById(conn, id));
            System.out.println("现在人数：" + StudentDao.count(conn));

            System.out.println("=== 5. 转账（事务 + 对账）===");
            StudentDao.ensureAccount(conn, "钱七", 1000);
            System.out.println("钱七→李四 转 300：" + StudentDao.transfer(conn, "钱七", "李四", 300));
            System.out.println("李四→钱七 转 99999：" + StudentDao.transfer(conn, "李四", "钱七", 99999));
            System.out.println("钱七：" + StudentDao.balanceOf(conn, "钱七")
                    + " 李四：" + StudentDao.balanceOf(conn, "李四")
                    + " 对账：" + StudentDao.audit(conn));
            // 期望：
            //   === 1. 单人报名：吴十 ===
            //   插入行数：1
            //   现在人数：8
            //   新人的 id：8
            //   === 2. 批量报名（事务）：三个人 ===
            //   批量插入行数：3
            //   现在人数：11
            //   === 3. 分页：每页 3 个，看第 1、2、3 页 ===
            //   第 1 页（3 条）：[张三 88 一班, 李四 95 一班, 王五 76 一班]
            //   第 2 页（3 条）：[赵六 60 二班, 钱七 100 二班, 孙八 45 三班]
            //   第 3 页（3 条）：[周九 82 三班, 吴十 90 二班, 郑十一 55 三班]
            //   === 4. 删掉一个人，再分页看总数 ===
            //   冯十三的 id：11，删除行数：1
            //   现在人数：10
            //   === 5. 转账（事务 + 对账）===
            //   钱七→李四 转 300：true
            //   李四→钱七 转 99999：false
            //   钱七：700 李四：1300 对账：5000
            //   💡 对账 4000 怎么来的（自己加一遍，别背）：
            //      张三 1000 + 李四 1300 + 王五 1000 + 钱七 700 = 4000
            //      钱七转给李四 300 只是"钱搬家"——搬完后总数和转账前一样（4000）
            //      第二次转 99999 失败 → 数字一个都不许变（这就是事务 + 对账的意义）
        }
    }
}

// ===== 你的代码写在这里：class Student + class StudentDao =====

// ① class Student —— public 字段 name/score/className + 构造器 + toString（"姓名 分数 班级"）
//
// ② class StudentDao —— 今天所有招式的合体，九个方法：
//
//    【写操作】
//    · static int insert(Connection conn, String name, int score, String cls)
//          INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)  → return executeUpdate()
//    · static int deleteById(Connection conn, int id)
//          DELETE FROM student WHERE id = ?                                → return executeUpdate()
//    · static int findId(Connection conn, String name)
//          SELECT id FROM student WHERE name = ?                          → 查不到 return -1
//    · static int lastInsertId(Connection conn)
//          SELECT LAST_INSERT_ID()   ← MySQL 给的"刚插进去那行的自增 id"
//          （也可以 SELECT MAX(id) FROM student，但并发下不严谨，LAST_INSERT_ID 才是标准答案）
//
//    【批量 + 事务】（Day27 Ex2 的 addBatch + Ex26 的事务，合起来用）
//    · static int addAll(Connection conn, List<Student> list)
//          空 list → 直接 return 0（别白开一个事务）
//          conn.setAutoCommit(false)
//          try { 循环 setXxx + addBatch → executeBatch() → conn.commit() → return list.size() }
//          catch (SQLException e) { conn.rollback(); throw e; }     ← 三个人里有一个炸，全部别进库
//          finally { conn.setAutoCommit(true); }
//
//    【分页查询】（真实项目的"列表页"就靠它）
//    · static List<Student> page(Connection conn, int pageNo, int pageSize)
//          SELECT name, score, class_name FROM student ORDER BY id LIMIT ? OFFSET ?
//          ⚠️ OFFSET = (pageNo - 1) * pageSize    ← 第 1 页从第 0 行开始
//             第 1 页 pageSize=3 → LIMIT 3 OFFSET 0；第 2 页 → LIMIT 3 OFFSET 3
//          超过范围（比如只有 10 人看第 9 页）→ 返回**空 List**，不报错
//          💡 必须指定 ORDER BY！否则数据库给的行序**不保证稳定**，翻页会出现"重复/漏行"
//
//    【事务转账】（Day27 Ex4/Ex5 的第三种风格：余额不足 return false，不抛异常）
//    · static boolean transfer(Connection conn, String from, String to, int amount)
//          查不到人 / 余额不足 → return false
//          成功 → setAutoCommit(false) → 两条 UPDATE → commit → return true
//          catch (SQLException e) { rollback(); return false; }
//          finally { setAutoCommit(true); }
//
//    【辅助】
//    · static void ensureAccount(Connection conn, String owner, int balance)
//          先查有没有（SELECT id FROM bank_account WHERE owner = ?），没有才 INSERT
//          （复用会报主键/重复数据，所以"先查后插"）
//    · static int balanceOf(Connection conn, String owner)      → 查不到 return -1
//    · static int count(Connection conn)                        → SELECT COUNT(*)
//    · static int audit(Connection conn)                        → SELECT SUM(balance)
//
// ③ 今天最值钱的三个"项目经验"（面试能说）：
//    1. **分页必须 ORDER BY**：不排序的 LIMIT/OFFSET 在数据变动时会出现重复或漏行
//    2. **批量操作套事务**：一半成功一半失败 = 脏数据，宁可全退
//    3. **对账兜底**：转账后 SUM 必须不变；这是你自己的"自动化测试"

// ===========================