import java.sql.*;

public class Day21_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：插入 1 行 → 把 id=1 的分数改成 59 → 打印受影响行数
        DBUtil.createTable();
        DBUtil.insertStudent("李四", 95);
        int n = DBUtil.updateScore(1, 59);
        System.out.println("更新成功，影响 " + n + " 行");   // 期望输出：更新成功，影响 1 行
    }
}

// ===== 你的代码写在这里：类 DBUtil——getConnection() + createTable() + insertStudent + updateScore(id, score) =====
class DBUtil {
    static final String URL = "jdbc:mysql://localhost:3306/day21?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8";
    static final String USER = "root";
    static final String PASSWORD = "root";
     public static Connection getConnection() throws SQLException{
        Connection conn=DriverManager.getConnection(URL,USER,PASSWORD);
        return conn;
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
        String insertSql="INSERT INTO student(name,score) VALUES(?,?)";
        try(
            Connection conn=getConnection();
            PreparedStatement ps=conn.prepareStatement(insertSql);
        ){
            ps.setString(1,name);
            ps.setInt(2,score);
            ps.executeUpdate();
        }
    }
    public static int updateScore(int id,int score) throws SQLException{
       String updateSql=" UPDATE student SET score=? WHERE id =?"; 
        try(
            Connection conn=getConnection();
            PreparedStatement ps=conn.prepareStatement(updateSql);
        ){
            ps.setInt(1,score);
            ps.setInt(2,id);
            return ps.executeUpdate();
        }
    }
    // TODO：getConnection() → DriverManager.getConnection(URL, "root", "root")
    // TODO：createTable()（同 Ex2）
    // TODO：insertStudent(name, score)（同 Ex3）

    // TODO：int updateScore(int id, int score) →

    //   UPDATE student SET score = ? WHERE id = ?；ps.executeUpdate() 的返回值 return 出去
}
// ===========================================