import java.sql.*;
import java.util.*;

public class Day28_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：手写迷你连接池——搞懂"池"到底池了什么
        try (Connection prepare = Db.getConnection()) {
            Setup.init(prepare);
        }

        MiniPool pool = new MiniPool(3);

        System.out.println("池子大小：" + pool.size());

        System.out.println("--- 借 3 个 ---");
        try (Connection c1 = pool.getConnection();
             Connection c2 = pool.getConnection();
             Connection c3 = pool.getConnection()) {
            System.out.println("池子还剩：" + pool.size());
            System.out.println("c1 能查到人数：" + countOf(c1));

            System.out.println("--- 借第 4 个（池子已经空了）---");
            Connection c4 = pool.getConnection();
            System.out.println("第 4 个是 null 吗：" + (c4 == null));

            System.out.println("--- 还回去 1 个（c2），再借一次 ---");
            c2.close();                 // ⚠️ 这里的 close() 不是真关闭，是"还回池子"（今天最妙的一手）
            System.out.println("池子还剩：" + pool.size());
            try (Connection c5 = pool.getConnection()) {
                System.out.println("还回去又借到的 c5 == 原来的 c2 吗：" + (c5 == c2));
                System.out.println("c5 也能查到人数：" + countOf(c5));
            }
        }
        // 期望（已真机验证）：
        //   池子大小：3
        //   --- 借 3 个 ---
        //   池子还剩：0
        //   c1 能查到人数：7
        //   --- 借第 4 个（池子已经空了）---
        //   第 4 个是 null 吗：true
        //   --- 还回去 1 个（c2），再借一次 ---
        //   池子还剩：1
        //   还回去又借到的 c5 == 原来的 c2 吗：false   ← ⚠️ 这个 false 是**正常的**！
        //   c5 也能查到人数：7
        //
        //   💡 为什么 c5 == c2 是 false 却说明"连接被复用了"？
        //      池子借给你的不是真连接，而是**每次现做的一个"壳"**（代理对象）；
        //      真连接复用了，但壳每次 `new` 一个新的，所以两个壳永远不相等。
        //      **要证明"真连接被复用"，看的是"池子还剩几个"（借完 0 → 还回 1 → 又借走 0）**。
        //      这就是 Ex5 要验证的事。
    }

    static int countOf(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM student")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}

// ===== 你的代码写在这里：class MiniPool =====

// MiniPool 要干的事（一共 3 个方法 + 1 个构造器，30 行以内）：
//
// ① 字段：
//      private final List<Connection> idle = new ArrayList<>();   // 空闲连接（用 Stack 也行）
//      private final int size;                                    // 池子容量
//
// ② 构造器 MiniPool(int size) throws SQLException
//      把这个 size 记下来，然后循环 size 次：
//      idle.add(Db.newConnection());     ← 池子一开始就把连接都建好（这就是"预热"）
//
// ③ Connection getConnection()
//      空闲列表是空的 → return null（借不到了；真实池子会等一会儿再超时报错）
//      否则 → 从列表里**移除并返回**一个：idle.remove(idle.size() - 1)
//
// ④ int size()
//      return idle.size();     ← main 打印的"池子还剩"就是这个
//
// ⑤ void close(Connection realConn) throws SQLException
//      把连接**还回池子**：idle.add(realConn)
//
// ★★ 最关键的一步（今天的灵魂）：⑥ 包装
//      main 里写的是 c2.close()，但如果直接返回真实连接，close() 就把连接**真关了**，
//      池子再还回来也没用（连接已经废了）。
//      所以要"套一层壳"：返回一个**代理对象**，它长得像 Connection，
//      但 close() 被换成了"还回池子"：
//
//      return (Connection) Proxy.newProxyInstance(
//              Connection.class.getClassLoader(),
//              new Class<?>[]{Connection.class},
//              (proxy, method, methodArgs) -> {
//                  if (method.getName().equals("close")) {
//                      this.close(real);          // 只拦截 close，其余原样转发
//                      return null;
//                  }
//                  return method.invoke(real, methodArgs);
//              });
//
//      要 import java.lang.reflect.Proxy。
//      💡 看不懂 Proxy 也没关系——**你要理解的是"池子借出去的是壳，真连接留在池子里"**。
//         这一步就是 HikariCP / Druid 这些连接池的核心把戏（它们叫"动态代理包装"）。
//
// ⚠️ 坑（真机验证过的后果）：如果 getConnection() 直接把 real 返回、不包壳，会连错两处：
//    ① `c2.close()` 真的把连接关了，**它不是"还回池子"，池子里的数量不会加**
//    ② 后面拿它执行 SQL 会报错：
//       java.sql.SQLException: No operations allowed after connection closed.
//    —— 所以"还回池子"这件事，只能靠包壳把 close() 换掉。这就是 Ex4 存在的全部理由。

// ===========================