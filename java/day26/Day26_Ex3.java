import java.sql.*;

public class Day26_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：参数化的"改"和"删"（executeUpdate 的返回值 = 受影响行数）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            System.out.println("一班加分受影响行数：" + UpdateDao.addScore(conn, "一班", 5));
            System.out.println("删除不及格受影响行数：" + UpdateDao.deleteBelow(conn, 60));

            System.out.println("--- 剩余 ---");
            Setup.printAll(conn);
            // 期望输出：
            //   一班加分受影响行数：3
            //   删除不及格受影响行数：1
            //   --- 剩余 ---
            //   张三 93 一班
            //   李四 100 一班
            //   王五 81 一班
            //   赵六 60 二班
            //   钱七 100 二班
            //   周九 82 三班
        }
    }
}

// ===== 你的代码写在这里：class UpdateDao =====

// TODO：两个方法都返回"受影响行数"（= ps.executeUpdate() 的返回值）
//   ① static int addScore(Connection conn, String cls, int delta)
//        UPDATE student SET score = score + ? WHERE class_name = ?
//        （注意 setInt 的顺序和 ? 一一对应）
//   ② static int deleteBelow(Connection conn, int min)
//        DELETE FROM student WHERE score < ?
//   ⚠️ SQL 里的 WHERE 千万不能漏——这里的 WHERE 也是"数据安全线"
// ===========================================
