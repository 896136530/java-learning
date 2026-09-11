import java.sql.*;

public class Day21_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：建表 student（id 自增主键 / name / score）。先 DROP 再 CREATE，重跑结果一致
        DBUtil.createTable();
        System.out.println("建表成功");      // 期望输出：建表成功
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    private static final String  DB_USER="root";
    private static  final String DB_PASSWORD="root";
    public static Connection getConnection() throws SQLException{
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        Connection conn=DriverManager.getConnection(URL,DB_USER,DB_PASSWORD);
        return conn;
    }
    public static void createTable() throws SQLException{
        try(
            Connection conn=getConnection();
            Statement stmt=conn.createStatement();
        ){
            String dropSql="DROP TABLE IF EXISTS student";
            stmt.execute(dropSql);

            String createSql="""
            CREATE  TAbLE student(
            id INT PRIMARY KEY AUTO_INCREMENT,
            name VARCHAR(50),
            score DOUBLE
            )
                    """;
                    stmt.execute(createSql);
        }
    } 
    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")

    // TODO：createTable() →
    //   1) DROP TABLE IF EXISTS student
    //   2) CREATE TABLE IF NOT EXISTS student (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(20), score INT)
}
// ===========================================