import java.sql.*;
import java.util.*;

public class Day26_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：排行榜——把 ORDER BY / LIMIT 搬进 SQL
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            for (String s : RankDao.ranking(conn, 3)) {
                System.out.println(s);
            }
            System.out.println("--- 前 5 ---");
            for (String s : RankDao.ranking(conn, 5)) {
                System.out.println(s);
            }
            // 期望输出：
            //   钱七 100
            //   李四 95
            //   张三 88
            //   --- 前 5 ---
            //   钱七 100
            //   李四 95
            //   张三 88
            //   周九 82
            //   王五 76
        }
    }
}

// ===== 你的代码写在这里：class RankDao =====

// TODO：static List<String> ranking(Connection conn, int limit)
//   · PreparedStatement：
//       SELECT name, score FROM student ORDER BY score DESC, name ASC LIMIT ?
//   · LIMIT 的 ? 也用 setInt(1, limit) 传（这就是参数化的好处：连"取几条"都能当参数）
//   · 返回 List<String>，每项 "姓名 分数"
//   ⚠️ 同分时要按姓名升序兜底（Day23 的老朋友），否则同分顺序不稳定
// ===========================================
