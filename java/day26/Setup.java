import java.sql.*;

/**
 * 固定的准备代码（**别改**）：
 *   init()     把 student 表恢复成初始 7 条数据（每道题开头都会调用，保证数据一致）
 *   count()    表里现在有多少人
 *   printAll() 按 id 顺序打印每个人的「姓名 分数 班级」
 */
public class Setup {

    public static int init(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS student");
            st.executeUpdate("CREATE TABLE student ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(20) NOT NULL, "
                    + "score INT DEFAULT 0, "
                    + "class_name VARCHAR(20)) DEFAULT CHARSET=utf8mb4");
        }

        String sql = "INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)";
        Object[][] data = {
                {"张三", 88, "一班"}, {"李四", 95, "一班"}, {"王五", 76, "一班"},
                {"赵六", 60, "二班"}, {"钱七", 100, "二班"},
                {"孙八", 45, "三班"}, {"周九", 82, "三班"}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] d : data) {
                ps.setString(1, (String) d[0]);
                ps.setInt(2, (Integer) d[1]);
                ps.setString(3, (String) d[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return count(conn);
    }

    public static int count(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM student")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public static void printAll(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, score, class_name FROM student ORDER BY id")) {
            while (rs.next()) {
                System.out.println(rs.getString("name") + " " + rs.getInt("score")
                        + " " + rs.getString("class_name"));
            }
        }
    }
}
