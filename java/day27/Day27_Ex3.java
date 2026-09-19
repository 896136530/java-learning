import java.sql.*;
import java.util.*;

public class Day27_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：多行 → List<Student>（对象映射）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            List<Student> top = StudentDao.findByMinScore(conn, 80);
            System.out.println("80 分以上：" + top.size() + " 人");
            for (Student s : top) {
                System.out.println(s);
            }

            List<Student> empty = StudentDao.findByMinScore(conn, 200);
            System.out.println("200 分以上：" + empty.size() + " 人（不报错）");
            // 期望输出：
            //   80 分以上：4 人
            //   钱七 100 二班
            //   李四 95 一班
            //   张三 88 一班
            //   周九 82 三班
            //   200 分以上：0 人（不报错）
        }
    }
}

// ===== 你的代码写在这里：class StudentDao =====

// static List<Student> findByMinScore(Connection conn, int min) throws SQLException
//   · SQL：SELECT name, score, class_name FROM student WHERE score >= ? ORDER BY score DESC, name ASC
//   · while (rs.next()) 逐行 → 每行交给 RowMapper.mapRow(rs) 变成 Student → 塞进 List
//   · ⚠️ RowMapper.mapRow 是 Ex1 写的（同一个目录、同一个包，直接用）
//        这说明"转换逻辑只写一次，读表的地方都复用它"——这就是 DAO 分层的味道
//   · 没查到时返回**空 List**（size 0），不是 null！返回 null 会让调用方每处都要判空
// ===========================================