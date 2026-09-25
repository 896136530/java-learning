package ex5;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;
import tool.Db;

/**
 * 题 5 的 main（**别改**）—— 命令行小书店：这是你今天要"发货"的成品。
 *
 * 逻辑很简单（没有菜单死循环，方便你反复跑、也方便自动测试）：
 *   1. 连库 → 建表 → 插入 3 本书
 *   2. 打印书目快照 + 库存总价值
 *   3. 按作者查一次（演示 WHERE）
 *   4. 如果**标准输入里有内容**，就再读一本书插进去，然后重新打印快照
 *      （直接用键盘跑：终端里输入一行 "书名 作者 价格 库存"，回车即可）
 *
 * ★ 为什么做成"读一行就结束"而不是"死循环菜单"？
 *   因为死循环的菜单**没法自动化验证**（Day27 记账本 Step3 就踩过：菜单 6 无出口导致编译报错）。
 *   能自动化 = 你能自己写脚本反复测，这比"看起来像真软件"更重要。
 *
 * 期望输出（已真机验证，5 本书、总价值 580.5 元）：
 *   ===== 命令行小书店（Maven 版）=====
 *   书目快照（共 5 本）：
 *     [1] Java 编程思想 / 王五 / 108.0 元 / 库存 2
 *     [2] 算法导论 / 李四 / 128.0 元 / 库存 1
 *     [3] 深入理解计算机系统 / 张三 / 139.0 元 / 库存 1
 *     [4] 数据结构与算法 / 李四 / 45.5 元 / 库存 1
 *     [5] MySQL 必知必会 / 张三 / 49.0 元 / 库存 1
 *   库存总价值：580.5 元
 *   （108×2 + 128 + 139 + 45.5 + 49 = 580.5）
 *   按作者「李四」查书：2 本
 *     [2] 算法导论 / 李四 / 128.0 元 / 库存 1
 *     [4] 数据结构与算法 / 李四 / 45.5 元 / 库存 1
 *   输入一本新书（书名 作者 价格 库存），直接回车跳过：
 *   跳过录入。
 *   最终书目快照（共 5 本）：
 *   ...
 */
public class BookApp {

    public static void main(String[] args) throws Exception {
        System.out.println("===== 命令行小书店（Maven 版）=====");

        try (Connection conn = Db.getConnection()) {
            BookDao.initTable(conn);
            List<Book> seed = List.of(
                    new Book("Java 编程思想", "王五", 108.0, 2),
                    new Book("算法导论", "李四", 128.0, 1),
                    new Book("深入理解计算机系统", "张三", 139.0, 1),
                    new Book("数据结构与算法", "李四", 45.5, 1),
                    new Book("MySQL 必知必会", "张三", 49.0, 1));
            for (Book b : seed) {
                BookDao.insert(conn, b);
            }

            printSnapshot(conn);

            List<Book> byAuthor = BookDao.findByAuthor(conn, "李四");
            System.out.println("按作者「李四」查书：" + byAuthor.size() + " 本");
            for (Book b : byAuthor) {
                System.out.println("  " + b);
            }

            System.out.print("输入一本新书（书名 作者 价格 库存），直接回车跳过：");
            Scanner sc = new Scanner(System.in);
            String line = sc.hasNextLine() ? sc.nextLine().trim() : "";
            if (line.isEmpty()) {
                System.out.println("跳过录入。");
            } else {
                String[] p = line.split("\\s+");
                if (p.length < 4) {
                    System.out.println("格式不对，需要 4 段（书名 作者 价格 库存），这次跳过。");
                } else {
                    Book b = new Book(p[0], p[1], Double.parseDouble(p[2]), Integer.parseInt(p[3]));
                    System.out.println("插入结果：" + BookDao.insert(conn, b) + " 行");
                }
            }
            System.out.println("最终" + snapshotLine(conn));
        }
    }

    static void printSnapshot(Connection conn) throws Exception {
        System.out.println(snapshotLine(conn));
        for (Book b : BookDao.findAll(conn)) {
            System.out.println("  " + b);
        }
        System.out.printf("库存总价值：%.1f 元%n", BookDao.totalValue(conn));
    }

    static String snapshotLine(Connection conn) throws Exception {
        return "书目快照（共 " + BookDao.count(conn) + " 本）：";
    }
}