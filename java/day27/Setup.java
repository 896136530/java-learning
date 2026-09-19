import java.sql.*;
import java.util.*;

/**
 * 固定的准备代码（**别改**）：
 *   init()      恢复两张表：student（7 人）、bank_account（3 个账户，各 1000 元）
 *   count()     学生表现在有多少人
 *   printAll()  按 id 顺序打印学生「姓名 分数 班级」
 *   printBanks()按卡号顺序打印账户「户名 余额」
 *   audit()     对账：返回所有账户余额合计（转账题用它验证“钱没多也没少”）
 */
public class Setup {

    public static void init(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS student");
            st.executeUpdate("CREATE TABLE student ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(20) NOT NULL, "
                    + "score INT DEFAULT 0, "
                    + "class_name VARCHAR(20)) DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("DROP TABLE IF EXISTS bank_account");
            st.executeUpdate("CREATE TABLE bank_account ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "owner VARCHAR(20) NOT NULL, "
                    + "balance INT NOT NULL DEFAULT 0) DEFAULT CHARSET=utf8mb4");
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

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bank_account (owner, balance) VALUES (?, ?)")) {
            String[] owners = {"张三", "李四", "王五"};
            for (String o : owners) {
                ps.setString(1, o);
                ps.setInt(2, 1000);
                ps.addBatch();
            }
            ps.executeBatch();
        }
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

    public static void printBanks(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT owner, balance FROM bank_account ORDER BY id")) {
            while (rs.next()) {
                System.out.println(rs.getString("owner") + "：" + rs.getInt("balance"));
            }
        }
    }

    /** 对账：所有账户余额合计 */
    public static int audit(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT SUM(balance) FROM bank_account")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** 按户名查余额（查不到返回 -1） */
    public static int balanceOf(Connection conn, String owner) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT balance FROM bank_account WHERE owner = ?")) {
            ps.setString(1, owner);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                }
                return -1;
            }
        }
    }
}