import java.sql.*;

public class Day21_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：连上 day21 数据库（MySQL 已装在本地：root/root）
        Connection c = DBUtil.getConnection();
        if (c != null && !c.isClosed()) {
            System.out.println("连接成功");   // 期望输出：连接成功
            c.close();
        }
    }
}

// ===== 你的代码写在这里：类 DBUtil——static Connection getConnection()（URL 常量已给出）=====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String DB_USER="root";
    private static final String DB_PASSWORD="root";
    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Connection conn=DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        return conn;
    }
}
