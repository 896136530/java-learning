package ex4;

/**
 * 题 4：打包 —— 但这次我先给你一个"会失败的任务"
 *
 * 目标：把项目打成一个**能直接 java -jar 跑**的文件。
 *
 * 做法（在 java/day29 目录下）：
 *   ① mvn package                     ← 普通打包，产物 target/day29-maven-1.0.jar
 *   ② java -jar target/day29-maven-1.0.jar
 *      → ❌ 你会看到报错！大概是：找不到或无法加载主类 / 或者 ClassNotFoundException
 *        （因为这个 jar 里**只有你写的类**，没有 MySQL 驱动，也没有 tool/ 之外的依赖）
 *   ③ 在 pom.xml 里启用 **maven-shade-plugin**（模板里已经写好了，你只要理解它，
 *      或者把它注释掉/放开各跑一次，亲眼看区别）
 *   ④ mvn package 再跑一次
 *      → 这次 target/ 里会出现两个 jar：
 *        · original-day29-maven-1.0.jar（瘦 jar，2666 字节级）
 *        · day29-maven-1.0.jar（**胖 jar，4 MB 级以上**，内含 MySQL 驱动）
 *   ⑤ java -jar target/day29-maven-1.0.jar → ✅ 成功打印 Ex1 的那段输出
 *
 * ★ 你的任务（这题不用写业务代码，全是"动手 + 观察"）：
 *   [ ] ① 先跑 mvn package，记下 target/ 里有什么、jar 多大
 *   [ ] ② java -jar 跑一次，把报错抄下来（这就是"瘦 jar 不能发货"）
 *   [ ] ③ 把 pom.xml 里 <artifactId>maven-shade-plugin</artifactId> 那个 <plugin> 整块注释掉，mvn package，看 jar 又变回瘦的
 *   [ ] ④ 放开注释，mvn package，这次两个 jar 都在 → java -jar 跑通
 *   [ ] ⑤ 回答一句话（写在下面的 TODO 注释里）：为什么 shade 之后的 jar 变大了 4 MB？
 *
 * 期望结果（已真机验证，本机实测值）：
 *   瘦 jar：original-day29-maven-1.0.jar ≈ 17.7 KB（17,695 字节）
 *   胖 jar：day29-maven-1.0.jar          ≈ 4.45 MB（4,448,212 字节 = 你的类 + MySQL 驱动 2.5 MB + 其他）
 *   java -jar 胖 jar → 正常打印 Ex1 的输出
 *
 * 💡 一句话理解：
 *   **jar 就是"打包好的文件夹"**。瘦 jar 只装了你的类；胖 jar（fat jar / uber jar）
 *   把用到的依赖一起装进去，所以它能"自己带着行李箱出门"，换台机器也能跑。
 *   以后 SpringBoot 打出来的也是这种可执行 jar（它内置了 Tomcat）。
 */
public class FatJar {

    // ===== TODO（你写）=====
    // 【观察记录】把上面 5 步的发现写在这里（中文随便写，写给自己看）：
    //
    // ① 第一次 mvn package 后，target/ 里有什么：
    //
    // ② 直接 java -jar 瘦 jar 的报错原文：
    //
    // ③ 注释掉 shade 插件后 jar 的大小：
    //
    // ④ 启用 shade 后两个 jar 各自多大：
    //
    // ⑤ 为什么胖 jar 大了 4 MB（一句话）：
    //
    // 💡 这题没有代码要写 —— 但**这 5 条记录才是本节的成果**。
    //    写不出来就说明步骤没做，那 Ex5 的"发货验证"会卡住。
    // ===========================

    public static void main(String[] args) {
        System.out.println("这个类不用运行——它的成果在 FatJar.java 的 TODO 记录里，");
        System.out.println("真正的验证是：java -jar target/day29-maven-1.0.jar 能跑起来。");
    }
}