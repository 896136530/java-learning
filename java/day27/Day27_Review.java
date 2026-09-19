import java.sql.*;
import java.util.*;

public class Day27_Review {
    public static void main(String[] args) throws Exception {
        // 综合：成绩管理 v2（实体类 + DAO + 事务三合一）
        try (Connection conn = Db.getConnection()) {
            Setup.init(conn);

            ScoreService.ensureAccount(conn, "钱七", 1000);
            ScoreService.ensureAccount(conn, "赵六", 1000);

            System.out.println("插入成功，当前人数：" + ScoreService.addStudent(conn, "吴十", 90, "二班"));

            System.out.println("--- 排行榜前 3 ---");
            List<Student> rank = ScoreService.ranking(conn);
            for (int i = 0; i < Math.min(3, rank.size()); i++) {
                System.out.println("第" + (i + 1) + "名：" + rank.get(i));
            }

            System.out.println("--- 每班人数 ---");
            for (Map.Entry<String, Integer> e : ScoreService.countByClass(conn).entrySet()) {
                System.out.println(e.getKey() + " " + e.getValue() + " 人");
            }

            System.out.println("--- 钱七 → 赵六 转 400 ---");
            System.out.println("转账结果：" + ScoreService.transfer(conn, "钱七", "赵六", 400));
            System.out.println("钱七：" + Setup.balanceOf(conn, "钱七")
                    + " 赵六：" + Setup.balanceOf(conn, "赵六"));
            System.out.println("对账：" + Setup.audit(conn));

            System.out.println("--- 钱七 → 赵六 转 99999（余额不足，必须不生效）---");
            System.out.println("转账结果：" + ScoreService.transfer(conn, "钱七", "赵六", 99999));
            System.out.println("钱七：" + Setup.balanceOf(conn, "钱七")
                    + " 赵六：" + Setup.balanceOf(conn, "赵六"));
            System.out.println("对账：" + Setup.audit(conn));
            // 期望输出：
            //   插入成功，当前人数：8
            //   --- 排行榜前 3 ---
            //   第1名：钱七 100 二班
            //   第2名：李四 95 一班
            //   第3名：吴十 90 二班
            //   --- 每班人数 ---
            //   一班 3 人
            //   二班 3 人
            //   三班 2 人
            //   --- 钱七 → 赵六 转 400 ---
            //   转账结果：true
            //   钱七：600 赵六：1400
            //   对账：5000
            //   --- 钱七 → 赵六 转 99999（余额不足，必须不生效）---
            //   转账结果：false
            //   钱七：600 赵六：1400
            //   对账：5000
            //   💡 对账为什么是 5000 不是 3000：main 开头给钱七、赵六各开了 1000 的账户，
            //      所以此刻银行总资产 = 张三/李四/王五 的 3000 + 钱七/赵六 的 2000 = 5000。
            //      转账只是"钱在账户间搬家"，所以两次转账之后对账**必须还是 5000**。
        }
    }
}

// ===== 你的代码写在这里：class ScoreService =====

// 五个方法（Day26 的四个 + Day27 的两个新能力）：
//
// ① static void ensureAccount(Connection conn, String owner, int balance)
//      先查这张表里有没有这个人（SELECT id FROM bank_account WHERE owner = ?）
//      没有才 INSERT INTO bank_account (owner, balance) VALUES (?, ?)
//      💡 为什么需要它：Setup.init() 只建了 张三/李四/王五 三个账户，
//         转账要用的钱七/赵六得自己加进银行表 —— 这就是真实项目里的"数据准备"
//
// ② static int addStudent(Connection conn, String name, int score, String cls)
//      参数化 INSERT → return Setup.count(conn);
//
// ③ static List<Student> ranking(Connection conn)
//      SELECT name, score, class_name FROM student ORDER BY score DESC, name ASC
//      每行用 RowMapper.mapRow(rs) 转成 Student（Ex1 写的，直接用——"转换逻辑只写一次"）
//
// ④ static Map<String,Integer> countByClass(Connection conn)
//      SELECT class_name, COUNT(*) FROM student GROUP BY class_name
//      ORDER BY COUNT(*) DESC, class_name
//      用 **LinkedHashMap** 装（HashMap 会打乱 SQL 排好的顺序）
//
// ⑤ static boolean transfer(Connection conn, String from, String to, int amount)
//      事务版转账，**用 boolean 通知成败**（Ex4 是抛异常、Ex5 是返回字符串，三种风格都见过了）：
//      · 付款方/收款方查不到（Setup.balanceOf 返回 -1）→ return false
//      · 余额不足 → return false
//      · 成功：setAutoCommit(false) → 扣钱 UPDATE + 加钱 UPDATE → commit() → return true
//      · catch SQLException → rollback(); return false
//      · finally → setAutoCommit(true)（conn 还给 main 前恢复原样）
//      💡 转账 400：钱七 1000→600、赵六 1000→1400，对账始终 3000
//         转账 99999 失败后：余额、对账**必须一个字都不变**（这就是事务的价值）
// ===========================================