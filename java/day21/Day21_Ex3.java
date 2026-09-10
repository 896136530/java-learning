import java.sql.*;

public class Day21_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：插入 3 行学生数据（必须用 PreparedStatement + ? 占位）
        DBUtil.createTable();
        DBUtil.insertStudent("张三", 88);
        DBUtil.insertStudent("李四", 95);
        DBUtil.insertStudent("王五", 76);
        System.out.println("插入3行成功");   // 期望输出：插入3行成功
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() + insertStudent(name, score) =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";

    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")

    // TODO：createTable()（同 Ex2）

    // TODO：insertStudent(String name, int score) →
    //   PreparedStatement ps = conn.prepareStatement("INSERT INTO student(name, score) VALUES(?, ?)");
    //   ps.setString(1, name);  ps.setInt(2, score);  ps.executeUpdate();
}
// ===========================================