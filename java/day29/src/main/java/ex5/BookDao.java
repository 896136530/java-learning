package ex5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 题 5：可执行 jar 实战 —— 把 Day27/28 的 DAO 搬进 Maven 工程
 *
 * 你的任务：补 5 个方法（下面都给了签名和口径，main 已经在调用它们）。
 * 写完之后：
 *   ① 运行Maven.bat 选 5（或 mvn -q exec:java -Dexec.mainClass=ex5.BookApp）→ 菜单交互
 *   ② mvn package → java -jar target/day29-maven-1.0.jar → **同样能跑**（这就是今天的成果）
 *
 * ⚠️ shade 插件里的 <mainClass> 现在写的是 ex1.HelloMaven。
 *    等你把 BookApp 写完，把它改成 **ex5.BookApp**，重新 mvn package，
 *    再 java -jar 一下 —— 命令行小书店就"发货"了。
 */
public class BookDao {

    /** 建表（先 DROP 再 CREATE，保证每次跑都是干净数据） */
    public static void initTable(Connection conn) throws SQLException {   // 已写好，别改
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS book");
            st.executeUpdate("CREATE TABLE book ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "title VARCHAR(50) NOT NULL, "
                    + "author VARCHAR(20) NOT NULL, "
                    + "price DOUBLE NOT NULL, "
                    + "stock INT NOT NULL) DEFAULT CHARSET=utf8mb4");
        }
    }

    /** 插入一本书 → return 影响行数 */
    public static int insert(Connection conn, Book b) throws SQLException {
        // TODO① ：INSERT INTO book (title, author, price, stock) VALUES (?, ?, ?, ?)
        //         setString/setString/setDouble/setInt → return executeUpdate()
        return 0;
    }

    /** 总数 → SELECT COUNT(*) FROM book */
    public static int count(Connection conn) throws SQLException {
        // TODO② ：参考 Day28 Ex2 的 countStudents
        return 0;
    }

    /** 按作者查书 → SELECT id, title, author, price, stock FROM book WHERE author = ? ORDER BY id */
    public static List<Book> findByAuthor(Connection conn, String author) throws SQLException {
        // TODO③ ：逐行 mapRow（下面给你写好了）塞进 List；查不到返回**空 List**
        return new ArrayList<>();
    }

    /** 库存总价值 → SELECT SUM(price * stock) FROM book */
    public static double totalValue(Connection conn) throws SQLException {
        // TODO④ ：聚合下推（Day28 学过：能让数据库算的别搬回 Java 算）
        //          💡 注意：如果表里一行都没有，SUM 返回 NULL，getDouble 会给你 0.0 —— 正好
        return 0.0;
    }

    /** 全部书 → SELECT id, title, author, price, stock FROM book ORDER BY id */
    public static List<Book> findAll(Connection conn) throws SQLException {
        // TODO⑤ ：和 TODO③ 几乎一样，只是去掉 WHERE
        //          💡 想偷懒？把 findByAuthor(conn, null) 里判空走全量也行 —— 但**那是不好的设计**，
        //             一个方法干两件事，以后没人看得懂。老老实实写两段（重复三行代码，换清晰）
        return new ArrayList<>();
    }

    /** 把结果集当前行变成 Book（已写好，别改） */
    static Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getDouble("price"),
                rs.getInt("stock"));
    }
}