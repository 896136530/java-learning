import java.sql.*;
import java.util.*;

public class Day27_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：批量插入（addBatch / executeBatch）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);
            System.out.println("插入前人数：" + Setup.count(conn));

            List<Student> newbies = Arrays.asList(
                    new Student("吴十", 90, "二班"),
                    new Student("郑十一", 55, "三班"),
                    new Student("王十二", 99, "一班"));

            int rows = BatchDao.insertAll(conn, newbies);
            System.out.println("插入行数：" + rows);
            System.out.println("插入后人数：" + Setup.count(conn));

            System.out.println("--- 全部学生 ---");
            Setup.printAll(conn);
            // 期望输出：
            //   插入前人数：7
            //   插入行数：3
            //   插入后人数：10
            //   --- 全部学生 ---
            //   张三 88 一班
            //   李四 95 一班
            //   王五 76 一班
            //   赵六 60 二班
            //   钱七 100 二班
            //   孙八 45 三班
            //   周九 82 三班
            //   吴十 90 二班
            //   郑十一 55 三班
            //   王十二 99 一班
        }
    }
}

// ===== 你的代码写在这里：class BatchDao =====

// static int insertAll(Connection conn, List<Student> list) throws SQLException
//   · SQL：INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)
//   · 先循环 setXxx 三件套 + ps.addBatch()，循环结束再 ps.executeBatch()
//   · ⚠️ 一个坑：3 个人 = 3 行，但 executeBatch() 返回的是 int[]（每行一条结果），
//        想返回"总共插了几行"，要么循环累加这个数组，要么直接 return list.size()
//   · 为什么不循环里直接 executeUpdate()？—— 每次执行都要一次网络往返，
//     批量是"打包送一趟"，1000 行时差几十倍（知识点里有）
class BatchDao {
    public static int insertAll(Connection conn, List<Student> list) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)")) {
            for (Student s : list) {
                ps.setString(1, s.getName());
                ps.setInt(2, s.getScore());
                ps.setString(3, s.getClassName());
                ps.addBatch();
            }
            int[] rows = ps.executeBatch();
            return list.size();
        }
    }
}
// ===========================================