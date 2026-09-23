import java.sql.*;

public class Day28_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：正式 DAO 的"写操作"三件套（增 / 改 / 删）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);
            System.out.println("初始人数：" + Setup.count(conn));

            System.out.println("--- 插入 吴十 90 二班 ---");
            System.out.println("影响行数：" + StudentUtils.insert(conn, "吴十", 90, "二班"));
            System.out.println("人数：" + Setup.count(conn));

            System.out.println("--- 把 吴十 的分数改成 100 ---");
            System.out.println("影响行数：" + StudentUtils.updateScore(conn, "吴十", 100));
            System.out.println("吴十的分数：" + StudentUtils.findOne(conn, "吴十").score);

            System.out.println("--- 改一个不存在的人 陈七 ---");
            System.out.println("影响行数：" + StudentUtils.updateScore(conn, "陈七", 100));
            // 期望："影响行数：0"（改不到人不是报错，是返回 0 —— 靠这个数判断有没有改到）

            System.out.println("--- 按 id 删除 吴十 ---");
            int id = StudentUtils.findId(conn, "吴十");
            System.out.println("吴十的 id：" + id);
            System.out.println("影响行数：" + StudentUtils.deleteById(conn, id));
            System.out.println("人数：" + Setup.count(conn));
            System.out.println("再查 吴十：" + StudentUtils.findOne(conn, "吴十"));
            // 期望最后："再查 吴十：null"
        }
    }
}

// ===== 你的代码写在这里：class Student + class StudentUtils =====

// ① class Student —— 三个 public 字段 + 一个两参构造器 + toString
//    · public String name; public int score; public String className;
//    · public Student(String name, int score, String className)
//    · toString() 返回 "姓名 分数 班级"（空格隔开）
//    · 今天字段可以不加 private（省掉 getter 的样板代码，练 SQL 为主）
//
// ② class StudentUtils —— 今天所有题共用的"小 DAO"
//    · static Student mapRow(ResultSet rs) throws SQLException
//          rs.getString("name") / rs.getInt("score") / rs.getString("class_name") → new Student(...)
//          （Day10 的 toString、Day27 的 mapRow 思路，今天不重复讲）
//    · static Student findOne(Connection conn, String name) throws SQLException
//          SELECT name, score, class_name FROM student WHERE name = ?
//          查到 → Student；查不到 → null（用 if (!rs.next()) return null;）
//    · static int findId(Connection conn, String name) throws SQLException
//          SELECT id FROM student WHERE name = ?
//          查到 → id；查不到 → -1
//    · static int insert(Connection conn, String name, int score, String cls) throws SQLException
//          INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)   → return executeUpdate()
//    · static int updateScore(Connection conn, String name, int score) throws SQLException
//          UPDATE student SET score = ? WHERE name = ?                       → return executeUpdate()
//    · static int deleteById(Connection conn, int id) throws SQLException
//          DELETE FROM student WHERE id = ?                                  → return executeUpdate()
//
// ⚠️ 三条写操作的返回值都是 **int（影响行数）**：
//    · 插入成功 → 1（插了 1 行）
//    · 改名不存在的人 → 0（**这不是异常**，是"没改到任何一行"）
//    · 项目里就是靠这个 0/1 判断"到底改没改到"，所以 DAO 方法**必须 return 它**

// ===========================