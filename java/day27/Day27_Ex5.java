import java.sql.*;

public class Day27_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：事务的三道防线（参数校验 / 回滚 / 对账）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            System.out.println("--- 转给不存在的人 ---");
            System.out.println("失败：" + SafeTransferService.tryTransfer(conn, "张三", "陈七", 100));
            System.out.println("张三余额：" + Setup.balanceOf(conn, "张三"));
            System.out.println("对账：" + Setup.audit(conn));

            System.out.println("--- 自己转给自己 ---");
            System.out.println("失败：" + SafeTransferService.tryTransfer(conn, "张三", "张三", 100));
            System.out.println("对账：" + Setup.audit(conn));

            System.out.println("--- 负数金额 ---");
            System.out.println("失败：" + SafeTransferService.tryTransfer(conn, "张三", "李四", -50));
            System.out.println("对账：" + Setup.audit(conn));

            System.out.println("--- 正常转账 200 ---");
            System.out.println("成功：" + SafeTransferService.tryTransfer(conn, "张三", "李四", 200));
            System.out.println("张三余额：" + Setup.balanceOf(conn, "张三"));
            System.out.println("李四余额：" + Setup.balanceOf(conn, "李四"));
            System.out.println("对账：" + Setup.audit(conn));
            // 期望输出：
            //   --- 转给不存在的人 ---
            //   失败：账户不存在
            //   张三余额：1000
            //   对账：3000
            //   --- 自己转给自己 ---
            //   失败：不能给自己转账
            //   对账：3000
            //   --- 负数金额 ---
            //   失败：金额必须大于 0
            //   对账：3000
            //   --- 正常转账 200 ---
            //   成功：true
            //   张三余额：800
            //   李四余额：1200
            //   对账：3000
        }
    }
}

// ===== 你的代码写在这里：class SafeTransferService =====

// static String tryTransfer(Connection conn, String from, String to, int amount) throws SQLException
//
// ⚠️ 和 Ex4 的 transfer 关键区别：Ex4 用**抛异常**通知失败，这题用**返回字符串**通知失败。
//    （两种风格项目里都常见：能用异常就别用返回值，但"是否算异常"有争议时返回结果码也合法）
//    · 成功 → return "true"
//    · 失败 → return 失败原因（main 会把它拼成 "失败：账户不存在" 这一行，所以
//      方法里**不要**自己 println，只 return 字符串）
//
// 校验顺序（都在开事务之前做，别浪费事务）：
//   ① amount <= 0               → return "金额必须大于 0"
//   ② from.equals(to)           → return "不能给自己转账"
//   ③ 付款方查不到               → return "账户不存在"
//   ④ 收款方查不到               → return "账户不存在"
//   ⑤ 付款方余额 < amount        → return "余额不足"
//   💡 查账户用 Setup.balanceOf(conn, owner)，查不到返回 -1
//
// 全过之后走事务：
//   conn.setAutoCommit(false) → 两条 UPDATE（扣钱 / 加钱） → conn.commit() → return "true"
//   · catch SQLException e → conn.rollback(); throw e;
//   · finally → conn.setAutoCommit(true)（把 conn 还给 main 时恢复原样）
// ⚠️ 只写 setAutoCommit(false) 忘了 commit → 数据一点没变（看着像"没执行"）
class SafeTransferService{
    public static String tryTransfer(Connection conn, String from, String to, int amount) throws SQLException {
        if(amount<=0){
            return "金额必须大于 0";
        }
        if(from.equals(to)){
            return "不能给自己转账";
        }
        if(Setup.balanceOf(conn,from)==-1){
            return "账户不存在";
        }
        if(Setup.balanceOf(conn,to)==-1){
                       return "账户不存在";
        }
        if(Setup.balanceOf(conn,from)<amount){
            return "余额不足";
        }
        conn.setAutoCommit(false);
        try{
            try(PreparedStatement pstmt = conn.prepareStatement("update bank_account set balance = balance - ? where owner = ?")){
                pstmt.setInt(1,amount);
                pstmt.setString(2,from);
                pstmt.executeUpdate();
            }
            try(PreparedStatement pstmt = conn.prepareStatement("update bank_account set balance = balance + ? where owner = ?")){
                pstmt.setInt(1,amount);
                pstmt.setString(2,to);
                pstmt.executeUpdate();
            }
            conn.commit();
            return "true";
        }catch(SQLException e){
            conn.rollback();
            throw e;
        }finally{
            conn.setAutoCommit(true);
        }
    }
}
