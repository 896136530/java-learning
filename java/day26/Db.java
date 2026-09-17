import java.sql.*;

/**
 * 固定的数据库工具类（**别改**）——所有练习都用它拿连接。
 *
 * 它做了两件事：
 *   1. 先连到"不指定库"的地址，确保 day26 这个库存在（第一次跑也不会报错）
 *   2. 再正式连到 day26，把连接交给你
 */
public class Db {
    private static final String BASE_URL =
            "jdbc:mysql://127.0.0.1:3306/?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/day26?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try (Connection boot = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement st = boot.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS day26 DEFAULT CHARACTER SET utf8mb4");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}
