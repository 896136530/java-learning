import java.sql.*;

public class Day21_Review {
    public static void main(String[] args) throws Exception {
        // Review：建→插5人→打印→删→改→数，一气呵成
        DBUtil.createTable();
        DBUtil.insertStudent("张三", 88);
        DBUtil.insertStudent("李四", 95);
        DBUtil.insertStudent("王五", 76);
        DBUtil.insertStudent("赵六", 60);
        DBUtil.insertStudent("钱七", 100);
        DBUtil.queryAll();
        int d = DBUtil.deleteById(3);
        System.out.println("删除 id=3 成功，影响 " + d + " 行");
        int u = DBUtil.updateScore(1, 100);
        System.out.println("更新 id=1 为 100 成功，影响 " + u + " 行");
        System.out.println("最终剩余 " + DBUtil.getUserCount() + " 人");
        // 期望输出：
        //   id=1 name=张三 score=88
        //   id=2 name=李四 score=95
        //   id=3 name=王五 score=76
        //   id=4 name=赵六 score=60
        //   id=5 name=钱七 score=100
        //   删除 id=3 成功，影响 1 行
        //   更新 id=1 为 100 成功，影响 1 行
        //   最终剩余 4 人
    }
}

// ===== 你的代码写在这里：类 DBUtil 全家桶 =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTable() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS student");
            stmt.execute("""
                    CREATE TABLE student(
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(20),
                    score INT
                    )
                    """);
        }
    }

    public static void insertStudent(String name, int score) throws SQLException {
        String sql = "INSERT INTO student(name, score) VALUES(?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, score);
            ps.executeUpdate();
        }
    }

    public static void queryAll() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student")) {
            while (rs.next()) {
                System.out.printf("id=%d name=%s score=%d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("score"));
            }
        }
    }

    public static int deleteById(int id) throws SQLException {
        String sql = "DELETE FROM student WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public static int updateScore(int id, int score) throws SQLException {
        String sql = "UPDATE student SET score = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, score);
            ps.setInt(2, id);
            return ps.executeUpdate();
        }
    }

    public static int getUserCount() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
// ===========================================