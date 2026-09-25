package tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 固定的数据库工具类（**别改**）——和 Day27/28 的 Db.java 一模一样的内容，
 * 但今天它的“身份”变了：它现在是 **Maven 项目里 src/main/java/tool/ 下的一个类**。
 *
 * ⚠️ 注意最后一件事：这个类 import 了 java.sql.*，而 java.sql 是 JDK 自带的；
 *    但它调用的 MySQL 驱动（com.mysql.cj.jdbc.Driver）**来自 pom.xml 里声明的依赖**——
 *    也就是说：**没有 pom.xml 里的那三行，这个类一跑就报"找不到驱动"。**
 */
public class Db {

    private static final String BASE_URL =
            "jdbc:mysql://127.0.0.1:3306/?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String DB_URL =
            "jdbc:mysql://127.0.0.1:3306/day29?useSSL=false&serverTimezone=Asia/Shanghai"
                    + "&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Db() {
    }

    /** 拿一个连接（会自动保证 day29 库存在） */
    public static Connection getConnection() throws SQLException {
        try (Connection boot = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement st = boot.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS day29 DEFAULT CHARACTER SET utf8mb4");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}