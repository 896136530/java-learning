import java.sql.*;
import java.util.*;

public class Day26_Review {
    public static void main(String[] args) throws Exception {
        // 综合：成绩管理系统 v1（插入 + 排行榜 + 平均分 + 每班人数）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            ScoreService.insert(conn, "吴十", 90, "二班");
            System.out.println("插入成功，当前人数：" + Setup.count(conn));

            System.out.println("--- 排行榜前 3 ---");
            List<String> rank = ScoreService.ranking(conn);
            for (int i = 0; i < Math.min(3, rank.size()); i++) {
                System.out.println("第" + (i + 1) + "名：" + rank.get(i));
            }

            System.out.println("全校平均分：" + ScoreService.avgAll(conn));

            System.out.println("--- 每班人数 ---");
            for (Map.Entry<String, Integer> e : ScoreService.countByClass(conn).entrySet()) {
                System.out.println(e.getKey() + " " + e.getValue() + " 人");
            }
            // 期望输出：
            //   插入成功，当前人数：8
            //   --- 排行榜前 3 ---
            //   第1名：钱七 100
            //   第2名：李四 95
            //   第3名：吴十 90
            //   全校平均分：79.50
            //   --- 每班人数 ---
            //   一班 3 人
            //   二班 3 人
            //   三班 2 人
        }
    }
}

// ===== 你的代码写在这里：class ScoreService =====

// TODO：四个方法（把今天前面的题拼起来）
//   ① static void insert(Connection conn, String name, int score, String cls)
//        参数化 INSERT（PreparedStatement 三个 ?）
//   ② static List<String> ranking(Connection conn)
//        全校学生，"姓名 分数"，ORDER BY score DESC, name ASC（不分页，全部返回）
//   ③ static String avgAll(Connection conn)
//        全校平均分，String.format("%.2f", ...)
//   ④ static Map<String,Integer> countByClass(Connection conn)
//        SELECT class_name, COUNT(*) FROM student GROUP BY class_name
//        ORDER BY COUNT(*) DESC, class_name
//        · 必须用 **LinkedHashMap** 装，才能保住 SQL 排好的顺序（HashMap 会乱）
// ===========================================
