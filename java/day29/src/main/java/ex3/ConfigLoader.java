package ex3;

import tool.Hold;

/**
 * 题 3：配置文件从哪来？—— 类路径（classpath） vs 磁盘路径
 *
 * 这题代码很短，但概念是今天最值钱的：
 *   ① src/main/resources/db.properties 这类文件，打包时会**进入 jar 的内部**
 *      → 只能靠类路径读（Hold.readResource）→ IDE 能跑、jar 也能跑
 *   ② 用 new FileInputStream("db.properties") 这种"磁盘相对路径"的读法
 *      → 在 IDE 里可能碰巧能跑（因为工作目录正好是项目根）
 *      → 打成 jar 之后**必然读不到**（jar 里没有"当前目录"这回事）
 *
 * 你的任务：写两个小方法，然后观察"两种读法输出一模一样，但第二种换成磁盘路径就完蛋"。
 *
 * 怎么跑：运行Maven.bat 选 3（或 mvn -q exec:java -Dexec.mainClass=ex3.ConfigLoader）
 *
 * 期望输出（已真机验证）：
 *   ===== 配置文件在 jar 里，还是磁盘上？=====
 *   --- 用【类路径】读（正确姿势）---
 *   读到 5 行，内容：
 *     db.user=root
 *     db.password=root
 *     db.pool.size=5
 *   ★ 结论：类路径读法在 IDE 里和 jar 里都能用。
 *   --- 用【磁盘路径】读（错误姿势）---
 *   当前工作目录：java\day29
 *   src\main\resources\db.properties 存在吗：true（IDE 里侥幸能读到）
 *   ⚠️ 打包成 jar 之后，这条路径就不存在了 —— 这就是"能跑不能发货"的典型病。
 *   ===== 一句话总结 =====
 *   跟着代码走的文件（配置/模板）→ 放 resources、用类路径读
 *   用户数据文件（账本.txt/导出.csv）→ 用磁盘路径读
 */
public class ConfigLoader {

    public static void main(String[] args) throws Exception {
        System.out.println("===== 配置文件在 jar 里，还是磁盘上？=====");

        System.out.println("--- 用【类路径】读（正确姿势）---");
        String content = loadFromClasspath("db.properties");
        String[] lines = content.strip().split("\n");
        System.out.println("读到 " + lines.length + " 行，内容：");
        for (String line : lines) {
            if (!line.startsWith("#")) {          // 注释行（# 开头）跳过
                System.out.println("  " + line.strip());
            }
        }
        System.out.println("★ 结论：类路径读法在 IDE 里和 jar 里都能用。");

        System.out.println("--- 用【磁盘路径】读（错误姿势）---");
        java.io.File f = new java.io.File("src/main/resources/db.properties");
        System.out.println("当前工作目录：" + System.getProperty("user.dir"));
        System.out.println("src/main/resources/db.properties 存在吗：" + f.exists()
                + (f.exists() ? "（IDE 里侥幸能读到）" : "（读不到！）"));
        System.out.println("⚠️ 打包成 jar 之后，这条路径就不存在了 —— 这就是\"能跑不能发货\"的典型病。");

        System.out.println("===== 一句话总结 =====");
        System.out.println("跟着代码走的文件（配置/模板）→ 放 resources、用类路径读");
        System.out.println("用户数据文件（账本.txt/导出.csv）→ 用磁盘路径读");
    }

    // ===== TODO（你写）=====
    //
    // ① static String loadFromClasspath(String name)
    //      → 直接转发给 Hold.readResource(name) 就行（一行）
    //      （✅ 这个 main 已经在调用它了，所以不写就编译不过 —— 先去写这一行）
    //
    // ② static int countLines(String content)
    //      → 数一数有几行"有效内容"（跳过 # 注释行和空行）
    //      → 提示：content.split("\n") 之后循环，用 line.strip() 去掉首尾空白
    //      → 然后自己把 main 里那句 "读到 N 行" 改成用它的结果
    //
    // 💡 自测：把 db.properties 里加一行 db.timeout=3000，再跑一次，看行数会不会变 6。
    //    如果没变 —— 说明你读的还是旧的编译产物，执行一下 mvn clean 再跑（这就是"clean"的用处）。

    // ===========================
}