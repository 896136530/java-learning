package ex2;

import java.sql.Connection;
import java.sql.SQLException;
import tool.Db;

/**
 * 题 2：跑起来 + 找依赖（第一次见识 Maven 的"本地仓库"）
 *
 * 这题 process 很短，重点在两个动作：
 *   ① 在 day29 目录下执行 mvn 命令，观察它自己下载东西
 *   ② 去 C:\Users\89613\.m2\repository\ 里把 mysql-connector-j 找出来 ——
 *      亲眼看到"Maven 把 jar 存在哪了"（以前你是手动下载丢进 java/day21/lib/）
 *
 * 你的任务：补两个小方法（下面 TODO），其余是 main（别改）。
 *
 * 怎么跑：运行Maven.bat 选 2（或 mvn -q exec:java -Dexec.mainClass=ex2.Database）
 *
 * 期望输出（已真机验证）：
 *   ===== 找依赖：驱动是"谁"送来的 =====
 *   驱动类名     ：com.mysql.cj.jdbc.Driver
 *   驱动加载成功 ：true
 *   连接 day29   ：true
 *   ===== 本地仓库 =====
 *   仓库位置     ：C:\Users\89613\.m2\repository
 *   驱动 jar 在  ：C:\Users\89613\.m2\repository\com\mysql\mysql-connector-j\8.4.0\
 *   jar 文件     ：mysql-connector-j-8.4.0.jar
 *   jar 大小     ：2533399 字节       ← 本机实测（约 2.4 MB），你跑出来应该是同一个数
 *   👉 记住这条路径的形状：group/artifact/version/
 *      com.mysql / mysql-connector-j / 8.4.0 —— 正好就是 pom.xml 里三行的倒序！
 */
public class Database {

    public static void main(String[] args) throws Exception {
        System.out.println("===== 找依赖：驱动是\"谁\"送来的 =====");
        String driverClass = getDriverClass();
        Class.forName(driverClass);
        System.out.println("驱动类名     ：" + driverClass);
        System.out.println("驱动加载成功 ：" + (Class.forName(driverClass) != null));

        boolean ok;
        try (Connection conn = Db.getConnection()) {
            ok = conn.isValid(3);          // isValid：问数据库"你还活着吗"（3 秒超时）
        }
        System.out.println("连接 day29   ：" + ok);

        System.out.println("===== 本地仓库 =====");
        String repo = System.getProperty("user.home") + "\\.m2\\repository";
        System.out.println("仓库位置     ：" + repo);
        System.out.println("驱动 jar 在  ：" + repo + "\\com\\mysql\\mysql-connector-j\\8.4.0\\");
        System.out.println("jar 文件     ：mysql-connector-j-8.4.0.jar");
        System.out.println("👉 记住这条路径的形状：group/artifact/version/");
        System.out.println("   com.mysql / mysql-connector-j / 8.4.0 —— 正好就是 pom.xml 里三行的倒序！");
    }

    // ===== TODO（你写）=====
    //
    // ① static String getDriverClass()
    //      → 直接 return "com.mysql.cj.jdbc.Driver";
    //      💡 写死字符串看起来"很傻"——但这正是你不会写 pom.xml 的代价：
    //         在真实项目里，这个类名由依赖包提供，你只需要在 pom.xml 里写 GAV，代码里连类名都不用写
    //         （Spring Boot 之后连 Class.forName 都不需要了）
    //
    // ② static long jarSize() throws Exception
    //      → 用 java.io.File 指向上面的 jar 路径，return 它的 length()（文件大小，字节）
    //      → 然后在 main 的"===== 本地仓库 ====="那一段里加一行打印：
    //         System.out.println("jar 大小     ：" + jarSize() + " 字节");
    //      → 看看这个 jar 有多大（2 MB 左右），想想"以前手动下载的那个 jar 是不是也是这个尺寸"
    //
    // 💡 自测：故意把 pom.xml 里 mysql 的 version 改成 9.9.9，再跑一次，看 Maven 报什么错；
    //    改回去再跑，就好了。这个过程就是"依赖找不到"时的真实体验。

    // ===========================
}