import java.sql.*;

/**
 * 固定的数据库工具类（**别改**）——所有练习都用它拿连接。
 *
 * Day28 在 Day27 基础上加了「隔离级别」这一课：
 *   连接默认是"自动提交 + 可重复读（REPEATABLE READ）"，
 *   今天要自己 on/off 自动提交、自己设置隔离级别，观察另一个连接看到的数据有什么不同。
 *
 * ⚠️ 今天连接池那两题是"自己手写一个迷你池"，用的还是这里的 DriverManager——
 *    等以后上 SpringBoot，这行会换成 HikariDataSource.getConnection()，你的业务代码一行都不用改。
 */
public class Db {
    private static final String BASE_URL =
            "jdbc:mysql://127.0.0.1:3306/?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/day28?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /** 拿一个连接（用 try-with-resources 包着用，用完自动还） */
    public static Connection getConnection() throws SQLException {
        try (Connection boot = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement st = boot.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS day28 DEFAULT CHARACTER SET utf8mb4");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    /** 直接新建连接（不走池）——连接池题用它做对比 */
    public static Connection newConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}