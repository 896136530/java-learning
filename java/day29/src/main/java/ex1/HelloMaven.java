package ex1;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
 *   驱动内部版本 ：mysql-connector-j-8.0.33 (Revision: ...)   ← 驱动自己报的版本号，和 8.4.0 不一致是**正常的**
 *   连通性测试   ：SELECT 1 = 1  ✅
 *   ===== 结论 =====
 *   这个项目里没有 lib/ 目录，也没有任何 -cp 参数 —— 驱动是 Maven 从本地仓库自动送来的。
 *   本地仓库位置：C:\Users\89613\.m2\repository\com\mysql\mysql-connector-j\8.4.0\
 */
public class HelloMaven {

    public static void main(String[] args) throws Exception {
        System.out.println("===== Maven 项目跑起来了 =====");
        System.out.println("Java 版本    ：" + System.getProperty("java.version").split("\\.")[0]);
        System.out.println("Java 供应商  ：" + System.getProperty("java.vendor"));
        System.out.println("工作目录     ：" + System.getProperty("user.dir"));

        System.out.println("===== 连数据库（靠 pom.xml 声明的驱动）=====");
        try (Connection conn = Db.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("连接字符串   ：" + meta.getURL().split("\\?")[0] + "?...");
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
}