import java.sql.*;

public class Day21_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：建表 → 插 3 行 → 查出来打印（顺序固定：id 从 1 开始）
        DBUtil.createTable();
        DBUtil.insertStudent("张三", 88);
        DBUtil.insertStudent("李四", 95);
        DBUtil.insertStudent("王五", 76);
        DBUtil.queryAll();
        // 期望输出：
        //   id=1 name=张三 score=88
        //   id=2 name=李四 score=95
        //   id=3 name=王五 score=76
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() + insertStudent + queryAll() =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";

    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")

    // TODO：createTable()（同 Ex2）
    // TODO：insertStudent(name, score)（同 Ex3）

    // TODO：queryAll() →
    //   SELECT * FROM student；while (rs.next()) 逐行打印：
    //   System.out.printf("id=%d name=%s score=%d%n", rs.getInt("id"), rs.getString("name"), rs.getInt("score"));
}
// ===========================================