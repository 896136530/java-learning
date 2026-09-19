import java.sql.*;

public class Day26_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：JDBC 第一次查询（Statement vs PreparedStatement）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            System.out.println("全校人数：" + BasicDao.countAll(conn));

            Integer s1 = BasicDao.findScoreByName(conn, "李四");
            System.out.println("李四：" + (s1 == null ? "无成绩" : s1));

            Integer s2 = BasicDao.findScoreByName(conn, "陈七");
            System.out.println("陈七：" + (s2 == null ? "无成绩" : s2));

            // ⚠️ 下面这个名字里藏着 SQL 片段——只有 PreparedStatement 才不会被它骗到
            Integer s3 = BasicDao.findScoreByName(conn, "王五' OR '1'='1");
            System.out.println("注入尝试：" + (s3 == null ? "无成绩" : s3));
            // 期望输出：
            //   全校人数：7
            //   李四：95
            //   陈七：无成绩
            //   注入尝试：无成绩
        }
    }
}

// ===== 你的代码写在这里：class BasicDao =====

// TODO：两个方法（都 throws SQLException）
//   ① static int countAll(Connection conn)
//        SELECT COUNT(*) FROM student → 读第一行第一列
//   ② static Integer findScoreByName(Connection conn, String name)
//        · 用 **PreparedStatement**：SELECT score FROM student WHERE name = ?
//        · setString(1, name) 之后 executeQuery，rs.next() 为 false 就返回 null
//        · 返回类型必须是 Integer（不是 int！）——查不到要能表达"没有"
class BasicDao{
    public static int countAll(Connection conn) throws SQLException{
        String sql="SELECT COUNT(*) FROM student";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            try(ResultSet rs=ps.executeQuery()){
                rs.next();
                int count=rs.getInt(1);
                return count;
            }
        }

    }
    public static Integer findScoreByName(Connection conn,String name) throws SQLException{
        String sql="SELECT score FROM student WHERE name=?";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,name);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1);
                }
                return null;
            }
        }
    }
}
// ===========================================
