import java.sql.*;

/**
 * 固定的数据准备类（**别改**）——每个练习开头调一次 Setup.init(conn)，保证数据是干净的。
 *
 * day28 库两张表：
 *   student       7 个人（张三/李四/王五/赵六/钱七/孙八/周九）
 *   bank_account  3 个账户（张三/李四/王五，各 1000 元）
 */
public class Setup {

    /** 恢复两张表到初始状态（会先删除再重建，所以可以反复调用） */
    public static void init(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS student");
            st.executeUpdate("CREATE TABLE student ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "name VARCHAR(20) NOT NULL, "
                    + "score INT NOT NULL, "
                    + "class_name VARCHAR(20) NOT NULL) DEFAULT CHARSET=utf8mb4");

            st.executeUpdate("DROP TABLE IF EXISTS bank_account");
            st.executeUpdate("CREATE TABLE bank_account ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "owner VARCHAR(20) NOT NULL, "
                    + "balance INT NOT NULL DEFAULT 0) DEFAULT CHARSET=utf8mb4");
        }

        String sql = "INSERT INTO student (name, score, class_name) VALUES (?, ?, ?)";
        Object[][] rows = {
                {"张三", 88, "一班"},
                {"李四", 95, "一班"},
                {"王五", 76, "一班"},
                {"赵六", 60, "二班"},
                {"钱七", 100, "二班"},
                {"孙八", 45, "三班"},
                {"周九", 82, "三班"}
        };
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] r : rows) {
                ps.setString(1, (String) r[0]);
                ps.setInt(2, (Integer) r[1]);
                ps.setString(3, (String) r[2]);
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

    /** 学生表现在有多少人 */
    public static int count(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM student")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** 按 id 顺序打印所有学生 */
    public static void printAll(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, score, class_name FROM student ORDER BY id")) {
            while (rs.next()) {
                System.out.println(rs.getString("name") + " " + rs.getInt("score") + " " + rs.getString("class_name"));
            }
        }
    }

    /** 对账：所有账户余额合计（转账题用它验证"钱没多也没少"） */
    public static int audit(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT SUM(balance) FROM bank_account")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** 查某人余额，查不到返回 -1 */
    public static int balanceOf(Connection conn, String owner) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT balance FROM bank_account WHERE owner = ?")) {
            ps.setString(1, owner);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return -1;
                }
                return rs.getInt("balance");
            }
        }
    }
}