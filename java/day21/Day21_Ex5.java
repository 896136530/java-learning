import java.sql.*;

public class Day21_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：插入 1 行 → 把 id=1 的分数改成 59 → 打印受影响行数
        DBUtil.createTable();
        DBUtil.insertStudent("李四", 95);
        int n = DBUtil.updateScore(1, 59);
        System.out.println("更新成功，影响 " + n + " 行");   // 期望输出：更新成功，影响 1 行
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() + insertStudent + updateScore(id, score) =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";

    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")
    // TODO：createTable()（同 Ex2）
    // TODO：insertStudent(name, score)（同 Ex3）

    // TODO：int updateScore(int id, int score) →
    //   UPDATE student SET score = ? WHERE id = ?；ps.executeUpdate() 的返回值 return 出去
}
// ===========================================