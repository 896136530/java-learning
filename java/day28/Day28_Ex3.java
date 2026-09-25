import java.sql.*;

public class Day28_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：事务隔离级别——两个人在同一时刻看同一条数据，看到的一样吗？
        try (Connection connA = Db.getConnection();
             Connection connB = Db.getConnection()) {

            Setup.init(connA);
            connB.setAutoCommit(true);          // B 用最普通的方式读（每条 SQL 立刻生效）

            System.out.println("初始：A 看张三=" + BalanceDao.balanceOf(connA, "张三")
                    + "，B 看张三=" + BalanceDao.balanceOf(connB, "张三"));
            System.out.println("A 的隔离级别：" + BalanceDao.levelName(connA.getTransactionIsolation()));

            // ===== 场景 1：A 改了但没提交，B 能看到吗？=====
            System.out.println("--- 场景1：A 把张三改成 700（先不提交）---");
            BalanceDao.openTx(connA);
            BalanceDao.updateBalance(connA, "张三", 700);
            System.out.println("A 自己看张三=" + BalanceDao.balanceOf(connA, "张三"));
            System.out.println("B 看张三=" + BalanceDao.balanceOf(connB, "张三"));
            BalanceDao.commit(connA);
            System.out.println("A 提交后，B 看张三=" + BalanceDao.balanceOf(connB, "张三"));

            // ===== 场景 2：A 改了又反悔（rollback），数据回到哪里？=====
            System.out.println("--- 场景2：A 把张三改成 500，然后 rollback ---");
            BalanceDao.openTx(connA);
            BalanceDao.updateBalance(connA, "张三", 500);
            System.out.println("A 未提交时看张三=" + BalanceDao.balanceOf(connA, "张三"));
            BalanceDao.rollback(connA);
            System.out.println("rollback 后 A 看张三=" + BalanceDao.balanceOf(connA, "张三"));

            // ===== 场景 3：换个隔离级别看看 =====
            System.out.println("--- 场景3：把 connB 降到 READ COMMITTED ---");
            connB.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            System.out.println("B 现在的隔离级别：" + BalanceDao.levelName(connB.getTransactionIsolation()));
        }
    }
}

// ===== 你的代码写在这里：class BalanceDao =====

// ★★ 今天的关键：MySQL 默认隔离级别是 **REPEATABLE READ（可重复读）**。
//    MySQL 的"一致性读"（快照读）让同一个事务里两次 SELECT 结果一样，
//    所以场景 1 里 B 会看到 **旧值 1000**（这就是"隔离"——B 活在 A 提交之前的快照里）。
//    你要做的就是把方法写出来，让程序跑起来、把真实数字打出来，亲眼看到这件事。

// 四个方法（全部 static，都在 BalanceDao 里）：
//
// ① static int balanceOf(Connection conn, String owner) throws SQLException
//       SELECT balance FROM bank_account WHERE owner = ?    → 查不到 return -1
//
// ② static int updateBalance(Connection conn, String owner, int balance) throws SQLException
//       UPDATE bank_account SET balance = ? WHERE owner = ?  → return executeUpdate()
//
// ③ static void openTx(Connection conn) throws SQLException
//       conn.setAutoCommit(false);      ← 开事务：从这里开始的 SQL 攒着不生效
//
// ④ static void commit(Connection conn) throws SQLException
//       conn.commit();                  ← 提交：一起生效
//
//    static void rollback(Connection conn) throws SQLException
//       conn.rollback();                ← 反悔：本次事务里的改动全部撤销
//
// ⑤ static String levelName(int level)
//       把数字翻译成人话（switch 或 if）：
//         Connection.TRANSACTION_READ_UNCOMMITTED → "READ UNCOMMITTED（读未提交）"
//         Connection.TRANSACTION_READ_COMMITTED   → "READ COMMITTED（读已提交）"
//         Connection.TRANSACTION_REPEATABLE_READ  → "REPEATABLE READ（可重复读）"
//         Connection.TRANSACTION_SERIALIZABLE     → "SERIALIZABLE（串行化）"
//         其它                                    → "未知(" + level + ")"
//       💡 常量值是 1 / 2 / 4 / 8，但**别背数字**，用 Connection.XXX 常量比
//
// ⚠️ 场景 2 里你会看到：A 未提交时自己看到 500，rollback 之后回到 700（场景 1 提交的值）。
//    **"自己能看到自己没提交的改动，别人看不到"** —— 这就是事务隔离的最直观样子。
class BalanceDao{
    public static  int balanceOf(Connection conn,String owner)throws SQLException{
        try(PreparedStatement ps=conn.prepareStatement("SELECT balance FROM bank_account WHERE owner = ?")){
            ps.setString(1,owner);
            try(ResultSet rs=ps.executeQuery()){
                if(rs.next()==false){
                    return -1;
                }
                return rs.getInt(1);
            }
        }
        }
    public static int updateBalance(Connection conn,String owner,int balance)throws SQLException{
        try(PreparedStatement ps=conn.prepareStatement("UPDATE bank_account SET balance = ? WHERE owner = ?")){
            ps.setInt(1,balance);
            ps.setString(2,owner);
            return ps.executeUpdate();
    }
}
    public static void openTx(Connection conn)throws SQLException{
        conn.setAutoCommit(false);
    }
    public static void commit(Connection conn)throws SQLException{
        conn.commit();
    }
    public static void rollback(Connection conn)throws SQLException{
        conn.rollback();
    }
    public static String levelName(int level){
        switch(level){
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                return "READ UNCOMMITTED（读未提交）";
            case Connection.TRANSACTION_READ_COMMITTED:
                return "READ COMMITTED（读已提交）";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "REPEATABLE READ（可重复读）";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "SERIALIZABLE（串行化）";
            default:
                return "未知(" + level + ")";
        }
    }
}

// ===========================  