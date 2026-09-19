import java.sql.*;
import java.util.*;

public class Day26_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：ResultSet → 把查出来的行组装成 List
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            List<String> a = ParamDao.findByClass(conn, "一班");
            System.out.println("一班 " + a.size() + " 人");
            for (String s : a) {
                System.out.println(s);
            }

            List<String> c = ParamDao.findByClass(conn, "三班");
            System.out.println("三班 " + c.size() + " 人");
            for (String s : c) {
                System.out.println(s);
            }
            // 期望输出：
            //   一班 3 人
            //   李四 95
            //   张三 88
            //   王五 76
            //   三班 2 人
            //   周九 82
            //   孙八 45
        }
    }
}

// ===== 你的代码写在这里：class ParamDao =====

// TODO：static List<String> findByClass(Connection conn, String cls)
//   · PreparedStatement：SELECT name, score FROM student WHERE class_name = ? ORDER BY score DESC
//   · while (rs.next()) 逐行读，每行拼成 "姓名 分数" 塞进 List，最后 return
//   · 提示：rs.getString("name") / rs.getInt("score")——按列名取，比按 1、2 更不容易错
class ParamDao{
    public static List<String> findByClass(Connection conn,String cls)  throws SQLException{
        String sql="SELECT name,score FROM student WHERE class_name=? ORDER BY score DESC";
        List<String>list=new ArrayList<>();
        try(PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,cls);
            try(ResultSet rs=ps.executeQuery()){
                while(rs.next()){
                    list.add(rs.getString("name")+" "+rs.getInt("score"));
                }
            }
        }
        return list;
    }
}
// ===========================================
