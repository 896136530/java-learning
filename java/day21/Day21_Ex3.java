import java.sql.*;

public class Day21_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：插入 3 行学生数据（必须用 PreparedStatement + ? 占位）
        DBUtil.createTable();
        DBUtil.insertStudent("张三", 88);
        DBUtil.insertStudent("李四", 95);
        DBUtil.insertStudent("王五", 76);
        System.out.println("插入3行成功");   // 期望输出：插入3行成功
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() + insertStudent(name, score) =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true";
    private static  final String  USER="root";
    private  static final String PASSWORD="root";
    public static Connection  getConnection() throws SQLException{
    Connection conn=    DriverManager.getConnection(URL,USER,PASSWORD);
        return  conn;
    }
    public static void createTable() throws SQLException{
        try(
            Connection conn=getConnection();
            Statement stmt=conn.createStatement();
        ){
            stmt.execute("DROP TABLE IF EXISTS student");
            stmt.execute("""
                    CREATE TABLE student(
                    id INT PRIMARY KEY AUTO_INCREMENT COMMENT'自增主键',
                    name VARCHAR(50) COMMENT'姓名',
                    score INT COMMENT'成绩'
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
                    """);
        }
    }
    public static void insertStudent(String name,int score) throws SQLException{
        String inserSql="INSERT INTO student(name,score) VALUES(?,?)";
        try(
            Connection conn=getConnection();
            PreparedStatement ps=conn.prepareStatement(inserSql);
        ){
            ps.setString(1,name);
            ps.setInt(2,score);
            int rows=ps.executeUpdate();
        }
    }
    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")

    // TODO：createTable()（同 Ex2）

    // TODO：insertStudent(String name, int score) →
    //   PreparedStatement ps = conn.prepareStatement("INSERT INTO student(name, score) VALUES(?, ?)");
    //   ps.setString(1, name);  ps.setInt(2, score);  ps.executeUpdate();
}
// ===========================================