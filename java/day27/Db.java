import java.sql.*;

/**
 * 固定的数据库工具类（**别改**）——所有练习都用它拿连接。
 *
 * Day27 比 Day26 多做了一件事：除了保证 day27 库存在，
 * 还负责把「用事务」这件事讲清楚——连接默认是自动提交（每条 SQL 立刻生效），
 * 要自己控制“要么全成功、要么全回滚”，就在业务代码里 setAutoCommit(false)。
 */
public class Db {
    private static final String BASE_URL =
            "jdbc:mysql://127.0.0.1:3306/?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/day27?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try (Connection boot = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement st = boot.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS day27 DEFAULT CHARACTER SET utf8mb4");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}