import java.sql.*;

public class Day26_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：聚合查询——让数据库算，Java 只负责拿结果
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            for (String cls : new String[]{"一班", "二班", "三班"}) {
                System.out.println(cls + "平均分：" + AggDao.avgOfClass(conn, cls));
            }
            System.out.println("全校平均分：" + AggDao.avgAll(conn));
            // 期望输出：
            //   一班平均分：86.33
            //   二班平均分：80.00
            //   三班平均分：63.50
            //   全校平均分：78.00
        }
    }
}

// ===== 你的代码写在这里：class AggDao =====

// TODO：两个方法，都返回"保留两位小数的字符串"
//   ① static String avgOfClass(Connection conn, String cls)
//        SELECT ROUND(AVG(score), 2) FROM student WHERE class_name = ?
//   ② static String avgAll(Connection conn)
//        SELECT ROUND(AVG(score), 2) FROM student
//   · 读出来用 rs.getDouble(1)（或 rs.getString(1)），返回前统一 String.format("%.2f", v)
//   · 想清楚：为什么"算平均"要交给 SQL，而不是把数据全查回 Java 再算？（数据量 100 万时差在哪）
class AggDao{
    public static String avgOfClass(Connection conn,String cls) throws SQLException{
        String sql="SELECT ROUND(AVG(score),2) FROM student WHERE class_name=?";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,cls);
            try(ResultSet rs=ps.executeQuery()){
                rs.next();
                double avg=rs.getDouble(1);
                return String.format("%.2f",avg);
            }
        }
    }
    public static String avgAll(Connection conn)throws SQLException{
        String sql="SELECT ROUND(AVG(score),2) FROM student";
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            try(ResultSet rs=ps.executeQuery()){
                rs.next();
                double avg=rs.getDouble(1);
                return String.format("%.2f",avg);
            }
        }
    }
}
// ===========================================
