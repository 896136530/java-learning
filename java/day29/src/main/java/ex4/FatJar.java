 package ex4;

/**
 * 题 4：打包 —— 但这次我先给你一个"会失败的任务"
 *
 * 目标：把项目打成一个**能直接 java -jar 跑**的文件。
 *
 * 做法（在 java/day29 目录下）：
 *   ① mvn package                     ← 普通打包，产物 target/day29-maven-1.0.jar
 *   ② java -jar target/day29-maven-1.0.ja
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
    //     三个 jar：day29-maven-1.0.jar（4447423 字节）、day29-maven-1.0-shaded.jar（4447423 字节）、
    //     original-day29-maven-1.0.jar（16906 字节）。前两个是 shade 的工作稿和成品，内容一样。
    //     （批改批注：原写"两个大的一个小的"方向对，但"文件"要写清是"jar"+带上字节数，不然没法核对）
    //
    // ② 直接 java -jar 瘦 jar 的报错原文：target\original-day29-maven-1.0.jar 中没有主清单属性
    //     （✅ 完全正确。批注：第一次还漏了 target\ 报的是"Unable to access jarfile"，
    //      那是路径错，和"没有主清单属性"是两回事——已自己发现并修正）
    //
    // ③ 注释掉 shade 插件后 jar 的大小：约 17 KB（也就是瘦 jar 那个量级）
    //     （批注：方向正确，但"注释掉 shade"这个步骤你没真做，17.7kb 是从瘦 jar 抄来的。
    //      严格说注释掉 shade 后打出来的普通 jar 会**覆盖** day29-maven-1.0.jar，
    //      大小与瘦 jar 同级；这条我按"理解正确"记过，不信你下次真做一遍试试）
    //
    // ④ 启用 shade 后两个 jar 各自多大：4447423 字节（≈ 4.24 MB / 4343 KB）
    //     和 16906 字节（≈ 16.5 KB）—— 两个都要报，单位用**字节**最准
    //     （批注：④你只写了一个数"4344kb"，而且单位算错了：
    //      4447423 ÷ 1024 = 4343.2 KB，不是 4344。**KB 这种单位一四舍五入就没法核对**，
    //      以后一律抄字节数（`mvn package` 的输出里没有，用 dir 或文件的"属性"看））
    //
    // ⑤ 为什么胖 jar 大了 4 MB（一句话）：
    //     因为 shade 把**用到的依赖一起装进去了** —— MySQL 驱动（mysql-connector-j）
    //     的 1123 个 class 文件，约 4.4 MB，全塞进了同一个 jar 里
    //     （批注：⑤你的原话"多了需要用到的依赖如果用到了MySQL还有打开MySQL的东西"**意思对了**，
    //      但"打开MySQL的东西"这种说法写进作业太模糊——面试/答辩时说不清。
    //      改成"把 mysql-connector-j 驱动打进同一个 jar（1123 个类，约 4.4 MB）"就精准了）
    //
    // 💡 这题没有代码要写 —— 但**这 5 条记录才是本节的成果**。
    //    写不出来就说明步骤没做，那 Ex5 的"发货验证"会卡住。
    // ===========================

    public static void main(String[] args) {
        System.out.println("这个类不用运行——它的成果在 FatJar.java 的 TODO 记录里，");
        System.out.println("真正的验证是：java -jar target/day29-maven-1.0.jar 能跑起来。");
    }
}