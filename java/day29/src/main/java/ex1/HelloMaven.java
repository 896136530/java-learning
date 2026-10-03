package ex1;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import tool.Db;

/**
 * 题 1：第一个 Maven 项目 —— 目标不是写业务代码，而是把这三件事跑通：
 *   ① 用 Maven 编译（不用再 javac）
 *   ② 用 Maven 运行（不用再配 -cp ...jar）
 *   ③ 从本地仓库拿到 mysql-connector-j（不用再手动下载 jar）
 *
 * ★ 你的任务只有一处 TODO：把 getUrl 补上（从连接里读元数据）。
 *
 * 怎么跑（在 java/day29 目录下）：
 *   运行Maven.bat 选 1        ← 推荐（本机一键脚本）
 *   或 mvn -q exec:java -Dexec.mainClass=ex1.HelloMaven
 *
 * 期望输出（已真机验证）：
 *   ===== Maven 项目跑起来了 =====
 *   Java 版本    ：21
 *   Java 供应商  ：Microsoft
 *   工作目录     ：（java/day29 的路径）
 *   ===== 连数据库（靠 pom.xml 声明的驱动）=====
 *   连接字符串   ：jdbc:mysql://127.0.0.1:3306/day29?...
 *   数据库产品   ：MySQL
 *   产品版本     ：8.4.5
 *   驱动包版本   ：mysql-connector-j-8.4.0（pom.xml 里声明的）
 *   驱动内部版本 ：mysql-connector-j-8.4.0 (Revision: 1c3f5c14...)   ← 和 pom 声明一致
 *   连通性测试   ：SELECT 1 = 1  ✅
 *   ===== 结论 =====
 *   这个项目里没有 lib/ 目录，也没有任何 -cp 参数 —— 驱动是 Maven 从本地仓库自动送来的。
 *   本地仓库位置：C:\Users\89613\.m2\repository\com\mysql\mysql-connector-j\8.4.0\
 *
 * ⭐⭐ 最重要的一个 Maven 规则（2026-09-26 你真踩到了）：
 *    **`mvn exec:java` 之前必须先编译，而 Maven 编译的是 `src/main/java` 下的【所有 .java 文件】。**
 *    所以只要**任何一题**里还有没写完的方法（编译不过），**整个项目都跑不起来**——
 *    你跑 Ex1 会看到 `ClassNotFoundException: ex1.HelloMaven`，它跟 Ex1 本身一点关系都没有。
 *    这就是为什么"没写的 TODO"也必须留一个只返回默认值的方法骨架（空壳），不能整个删掉。
 *
 * ⚠️ 踩坑记录（我生成时踩过，你要知道）：
 *    如果你看到"驱动内部版本"显示 **8.0.33**，那说明你的 classpath 里混进了 **day21/lib 里那个旧驱动**——
 *    用 `mvn exec:java` 跑就该是 8.4.0（.m2 仓库里那个）。**版本号显示异常 = 依赖来源不对**，
 *    这是 Maven 项目排查问题的第一个抓手（`mvn dependency:tree` 能列出真正用了哪些依赖）。
 */
public class HelloMaven {

    public static void main(String[] args) throws Exception {
        try {
            run();
        } catch (SQLException e) {
            System.out.println();
            System.out.println("❌ 连不上数据库：" + e.getMessage());
            System.out.println("   → 先确认 MySQL 起来了：运行Maven.bat 会自动拉起来（等 10 秒）；");
            System.out.println("     也可以自己敲：E:\\mysql\\bin\\mysqld.exe --defaults-file=E:\\mysql\\my.ini");
        }
    }

    static void run() throws Exception {
        System.out.println("===== Maven 项目跑起来了 =====");
        System.out.println("Java 版本    ：" + System.getProperty("java.version").split("\\.")[0]);
        System.out.println("Java 供应商  ：" + System.getProperty("java.vendor"));
        System.out.println("工作目录     ：" + System.getProperty("user.dir"));

        System.out.println("===== 连数据库（靠 pom.xml 声明的驱动）=====");
        try (Connection conn = Db.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("连接字符串   ：" + getUrl(conn).split("\\?")[0] + "?...");
            System.out.println("数据库产品   ：" + meta.getDatabaseProductName());
            System.out.println("产品版本     ：" + meta.getDatabaseProductVersion());
            System.out.println("驱动包版本   ：mysql-connector-j-8.4.0（pom.xml 里声明的）");
            System.out.println("驱动内部版本 ：" + meta.getDriverVersion());

            try (var st = conn.createStatement();
                 var rs = st.executeQuery("SELECT 1")) {
                rs.next();
                System.out.println("连通性测试   ：SELECT 1 = " + rs.getInt(1) + "  ✅");
            }
        }

        System.out.println("===== 结论 =====");
        System.out.println("这个项目里没有 lib/ 目录，也没有任何 -cp 参数 —— 驱动是 Maven 从本地仓库自动送来的。");
        System.out.println("本地仓库位置：" + System.getProperty("user.home")
                + "\\.m2\\repository\\com\\mysql\\mysql-connector-j\\8.4.0\\");
    }

    // ===== TODO（你写）=====
    // 上面已经直接用了 meta.getURL()，所以你其实"没东西要写"——那就在下面**新增一个方法**练手吧：
    //
    //   static String getUrl(Connection conn) throws SQLException {
    //       return conn.getMetaData().getURL();
    //   }
    //
    // 然后把 main 里那行改成：System.out.println("连接字符串   ：" + getUrl(conn).split("\\?")[0] + "?...");
    //
    // 💡 为什么要这么绕一下：让你亲手敲一次"类 + 方法 + 调用"，确认 Maven 编译的确实是你写的这份源代码
    //    （如果你改了代码但输出没变，就说明 mvn 没编译到你的改动 —— 这是新手最常见的困惑）
    public static String getUrl(Connection conn) throws SQLException{
        return conn.getMetaData().getURL();
    }
}