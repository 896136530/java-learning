import java.sql.*;

public class Day27_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：事务——转账必须"同生共死"
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);
            // 钱七、赵六 是 Setup 之外的人，先把他们的账户开出来（各 1000）
            openAccount(conn, "钱七", 1000);
            openAccount(conn, "赵六", 1000);
            System.out.println("初始：钱七 " + Setup.balanceOf(conn, "钱七")
                    + " 赵六 " + Setup.balanceOf(conn, "赵六") + " 对账：" + Setup.audit(conn));

            System.out.println("--- 钱七 → 赵六 转 400 ---");
            System.out.println("返回：" + TransferService.transfer(conn, "钱七", "赵六", 400));
            System.out.println("钱七：" + Setup.balanceOf(conn, "钱七")
                    + " 赵六：" + Setup.balanceOf(conn, "赵六"));
            System.out.println("对账：" + Setup.audit(conn));

            System.out.println("--- 赵六 → 钱七 转 99999（余额不够）---");
            try {
                TransferService.transfer(conn, "赵六", "钱七", 99999);
                System.out.println("居然成功了？说明没做余额校验");
            } catch (SQLException e) {
                System.out.println("转账失败：" + e.getMessage());
            }
            System.out.println("钱七：" + Setup.balanceOf(conn, "钱七")
                    + " 赵六：" + Setup.balanceOf(conn, "赵六"));
            System.out.println("对账：" + Setup.audit(conn));
            // 期望输出：
            //   初始：钱七 1000 赵六 1000 对账：5000
            //   --- 钱七 → 赵六 转 400 ---
            //   返回：true
            //   钱七：600 赵六：1400
            //   对账：5000
            //   --- 赵六 → 钱七 转 99999（余额不够）---
            //   转账失败：余额不足
            //   钱七：600 赵六：1400
            //   对账：5000
            //   💡 对账 5000 = 张三/李四/王五 各 1000 + 钱七/赵六 各 1000。
            //      转账只是钱**搬家**，所以任何一次转账前后，这个总数都必须一模一样。
        }
    }

    /** main 用的小工具（已写好，别改）：没有这个账户就开一个 */
    static void openAccount(Connection conn, String owner, int balance) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO bank_account (owner, balance) VALUES (?, ?)")) {
            ps.setString(1, owner);
            ps.setInt(2, balance);
            ps.executeUpdate();
        }
    }
}

// ===== 你的代码写在这里：class TransferService =====

// static boolean transfer(Connection conn, String from, String to, int amount) throws SQLException
//
// 步骤（顺序很重要）：
//   ① 查付款方余额（SELECT balance FROM bank_account WHERE owner = ?）
//      · 查不到人 → 抛 new SQLException("账户不存在")
//      · 余额 < amount → 抛 new SQLException("余额不足")      ← 注意用**抛异常**通知调用方，
//        因为 return false 的写法里，"失败"和"成功"只差一个 boolean，容易漏判
//   ② conn.setAutoCommit(false);       ← 从这里开始，SQL 不立刻生效，攒着等你发话
//   ③ 扣钱：UPDATE bank_account SET balance = balance - ? WHERE owner = ?
//      加钱：UPDATE bank_account SET balance = balance + ? WHERE owner = ?
//   ④ 两次都成功 → conn.commit();      ← 一起生效（这两步之间断电，MySQL 也不会只生效一半）
//   ⑤ 任何一步报错 → catch 里 conn.rollback(); 然后把异常继续抛出去 throw e;
//      （finally 里别忘了把 setAutoCommit(true) 还回去——conn 是 main 借给你的，要恢复原样）
//
// 一句话理解：**事务 = 给一组 SQL 买"全退保险"**。中间任何一步炸了，整组一起撤销。
// ⚠️ 最经典的错：只写了 setAutoCommit(false) 忘了 commit → 程序跑完数据一点没变（看着像"没执行"）
// ===========================================