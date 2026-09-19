import java.sql.*;

public class Day27_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：一行数据 → 一个对象（rowToStudent）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            Student s = RowMapper.one(conn, "张三");
            System.out.println(s);                 // 要靠 toString()

            System.out.println("分数：" + s.getScore());
            System.out.println("班级：" + s.getClassName());

            System.out.println("查不到的人：" + RowMapper.one(conn, "陈七"));
            // 期望输出：
            //   张三 88 一班
            //   分数：88
            //   班级：一班
            //   查不到的人：null
        }
    }
}

// ===== 你的代码写在这里：class Student + class RowMapper =====

// ① class Student —— 三个字段 + 构造器 + 三个 getter + toString
//    · private String name / private int score / private String className
//    · public Student(String name, int score, String className)
//    · public String getName() / public int getScore() / public String getClassName()
//    · toString() 返回 "姓名 分数 班级"（用空格隔开），例如 张三 88 一班
//      （Day10 的老朋友——不写 toString，打印出来是 Student@1b6d3586）

// ② class RowMapper —— 把结果集的一行变成 Student
//    · static Student mapRow(ResultSet rs) throws SQLException
//        按列名取：rs.getString("name") / rs.getInt("score") / rs.getString("class_name")
//        然后 new Student(...) 返回
//    · static Student one(Connection conn, String name) throws SQLException
//        PreparedStatement：SELECT name, score, class_name FROM student WHERE name = ?
//        rs.next() 为 false 就 return null（List 版在 Ex3，这里只取一条）
//        ⚠️ 别在返回 null 的地方顺手把 conn 关了（conn 是 main 给的）
// ===========================================