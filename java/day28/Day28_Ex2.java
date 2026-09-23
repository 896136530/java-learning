import java.sql.*;

public class Day28_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：聚合查询下推——让数据库算，别捞回 Java 再算
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            System.out.println("人数：" + StudentUtils.countStudents(conn));
            System.out.println("平均分：" + StudentUtils.avgScore(conn));
            System.out.println("最高分：" + StudentUtils.maxScore(conn));
            System.out.println("--- 前 3 名 ---");
            for (Student s : StudentUtils.topN(conn, 3)) {
                System.out.println(s);
            }
            System.out.println("--- 前 10 名（比人数还多）---");
            System.out.println("拿到 " + StudentUtils.topN(conn, 10).size() + " 人");

            System.out.println("--- 各班人数 ---");
            System.out.println(StudentUtils.countByClass(conn));
            // 期望：
            //   人数：7
            //   平均分：78.00
            //   最高分：100
            //   --- 前 3 名 ---
            //   钱七 100 二班
            //   李四 95 一班
            //   张三 88 一班
            //   --- 前 10 名（比人数还多）---
            //   拿到 7 人              ← 数据库只给 7 行，客户端自己不报错
            //   --- 各班人数 ---
            //   {一班=3, 二班=2, 三班=2}    ← 顺序 = 各班第一个人的 id（一班1 / 二班4 / 三班6）
            //     ⚠️ 如果 SQL 写成 ORDER BY COUNT(*) DESC, class_name，你实际会看到
            //        {一班=3, 三班=2, 二班=2} —— "三"排在"二"前面！
            //        原因：中文的排序规则（collation）不看拼音也不看笔画，是按编码值排的，
            //        "三"的 UTF-8 编码比"二"小，所以排前面。**别依赖中文排序**，
            //        想"按班级首次出现的顺序"就用 ORDER BY COUNT(*) DESC, MIN(id)（本题就要求这个）。
        }
    }
}

// ===== 你的代码写在这里：class Student + class StudentUtils =====

// ① class Student —— 和 Ex1 一样（public 字段 ×3 + 构造器 + toString）
//
// ② class StudentUtils —— 六个"只读"方法
//    · static int countStudents(Connection conn)
//          SELECT COUNT(*) FROM student                 → rs.getInt(1)
//    · static double avgScore(Connection conn)
//          SELECT AVG(score) FROM student               → rs.getDouble(1)
//          💡 数据库算出来可能是 78.0 这种，main 里用字符串格式化打成两位小数；
//             你只要 return 出来就行（AVG 返回 NULL 时值是 0.0，本表不会空）
//    · static int maxScore(Connection conn)
//          SELECT MAX(score) FROM student
//    · static List<Student> topN(Connection conn, int n)
//          SELECT name, score, class_name FROM student ORDER BY score DESC, name ASC LIMIT ?
//          ⚠️ 两个新东西：
//             ① ORDER BY ... LIMIT ? —— LIMIT 也能用占位符，ps.setInt(1, n) 就行
//             ② 按 **id 插入顺序** 读回来 → 用 ArrayList；本方法要按分数排序，
//                所以 SQL 里已经有 ORDER BY，你只管 while (rs.next()) 逐行 mapRow 塞进 List
//          查不到（n 超过人数只是少几行）→ 返回**空 List 或短 List**，绝不返回 null
//    · static Map<String,Integer> countByClass(Connection conn)
//          SELECT class_name, COUNT(*) FROM student GROUP BY class_name ORDER BY COUNT(*) DESC, MIN(id)
//          ⚠️ 必须用 **LinkedHashMap** 装：HashMap 会打乱 SQL 排好的顺序，
//             打印出来就是 {二班=2, 一班=3, 三班=2} 这种"看着像乱序"的结果
//          💡 为什么 tie-break 用 MIN(id) 而不是 class_name：人数一样（2=2）时，
//             用 class_name 排会得到"三班 在 二班 前面"这种反直觉结果（中文按编码排，不是拼音）；
//             用 MIN(id)（= 该班第一个人的 id）就得到"一班、二班、三班"这种自然顺序。
//             👉 一句话：**中文别当排序键，用 id 最稳。**
//
// ③ 一个概念：**聚合下推**（aggregate pushdown）
//    "人数/平均分/最高分/前 N 名"这些活儿，数据库一行 SQL 就能算完；
//    如果你 SELECT * 全捞回 Java 再用循环统计，数据量一大（几十万行）就慢得离谱。
//    口诀：**能在数据库算的，就别搬回内存算。**

// ===========================