package ex5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import tool.Db;

/**
 * 🎯 复习：把 Day29 学的东西合起来做一遍（Maven 项目 + 配置文件 + 可发货 jar）
 *
 * 这是本章的"收口题"——不写新知识，只是把 Ex1~Ex5 用过的东西自己用一遍：
 *   · Maven 目录结构（这题就在 src/main/java/ex5/ 里）
 *   · 读 resources 配置（复习 Ex3）
 *   · DAO + JDBC（复习 Day27/28 和 Ex5）
 *   · 让整个程序能被打成一个能发货的 jar
 *
 * 你的任务：补 4 个方法（下面标了 TODO），main 已写好**别改**。
 *
 * ───────────────────────── 场景：命令行账单小工具 ─────────────────────────
 *
 * 输入：第一行一个整数 n；接下来 n 行，每行 `商品名 单价 数量`（单价是小数，数量是整数）
 * 输出：
 *   第 1 段：每个商品一行 —— 商品名 单价 数量 小计      （小计 = 单价 × 数量）
 *   第 2 段：合计 x 元                                （所有小计之和，保留 2 位小数）
 *   第 3 段：数据库里查回来的条数 y                     （证明数据真的存进去了）
 *   第 4 段：配置里写的库名 z                           （从 db.properties 读的 db.url）
 *
 * 期望输出示例（输入 3 行：苹果 5.5 2 / 牛奶 12.0 1 / 面包 8.25 4）：
 *   ===== 账单小工具（Maven 版）=====
 *   苹果 5.5 2 小计 11.0
 *   牛奶 12.0 1 小计 12.0
 *   面包 8.25 4 小计 33.0
 *   合计 56.00 元
 *   数据库里查到 3 条
 *   配置里的库地址 jdbc:mysql://127.0.0.1:3306/day29
 *   ===== 发货验收 =====
 *   想让它能发货：把 pom.xml 里 shade 的 mainClass 改成 ex5.ReviewThen
 *   然后 mvn clean package，再 java -jar target/day29-maven-1.0.jar
 *
 * ⚠️ 注意：类名叫 ReviewThen（不是 Review）——因为 Java 里不能有叫 Review 的类跟别的重名，
 *    而且这个名字更直白：Review Then Ship（复习完就发货）。
 */
public class ReviewThen {

    public static void main(String[] args) throws Exception {
        System.out.println("===== 账单小工具（Maven 版）=====");

        // ---- 读输入 ----
        java.util.Scanner sc = new java.util.Scanner(System.in);
        int n = sc.hasNextInt() ? Integer.parseInt(sc.next()) : 0;
        sc.nextLine();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String line = sc.hasNextLine() ? sc.nextLine().trim() : "";
            if (line.isEmpty()) {
                continue;
            }
            String[] p = line.split("\\s+");
            items.add(new Item(p[0], Double.parseDouble(p[1]), Integer.parseInt(p[2])));
        }

        // ---- 第 1 段：逐条打印 ----
        for (Item it : items) {
            System.out.printf("%s %s %d 小计 %.1f%n", it.name, it.price, it.count, subtotal(it));
        }

        // ---- 第 2 段：合计 ----
        System.out.printf("合计 %.2f 元%n", total(items));

        // ---- 第 3 段 + 第 4 段：入库 + 读配置 ----
        try (Connection conn = Db.getConnection()) {
            initTable(conn);
            for (Item it : items) {
                insert(conn, it);
            }
            System.out.println("数据库里查到 " + count(conn) + " 条");
        }
        System.out.println("配置里的库地址 " + dbUrl());

        System.out.println("===== 发货验收 =====");
        System.out.println("想让它能发货：把 pom.xml 里 shade 的 mainClass 改成 ex5.ReviewThen");
        System.out.println("然后 mvn clean package，再 java -jar target/day29-maven-1.0.jar");
    }

    // ===== TODO（你写这 4 个）=====================

    /** ① 一行的小计：单价 × 数量（注意返回 double） */
    static double subtotal(Item it) {
        return 0;                     // TODO
    }

    /** ② 所有小计之和（用循环累加，别用流，练基础） */
    static double total(List<Item> items) {
        return 0;                     // TODO
    }

    /** ③ 建表 + 插入 + 计数（复习 Day27/28 的 DAO 写法，三个小方法都写在这里面也行） */
    static void initTable(Connection conn) throws SQLException {
        // 提示：DROP TABLE IF EXISTS bill; CREATE TABLE bill(id INT PRIMARY KEY AUTO_INCREMENT,
        //       name VARCHAR(30), price DOUBLE, count_num INT) DEFAULT CHARSET=utf8mb4
    }

    static int insert(Connection conn, Item it) throws SQLException {
        return 0;                     // TODO：INSERT INTO bill (name, price, count_num) VALUES (?,?,?)
    }

    static int count(Connection conn) throws SQLException {
        return 0;                     // TODO：SELECT COUNT(*) FROM bill
    }

    /** ④ 从 resources 里读库地址（复习 Ex3：类路径读法，一行转发给 Hold）*/
    static String dbUrl() {
        // 提示：tool.Hold.readResource("db.properties") 拿到全文，
        //       再找出 db.url= 那一行（提示：split("\n") 后逐行判断 startsWith("db.url=")）
        return "";                    // TODO
    }

    // ==============================================

    /** 商品（已写好，别改） */
    static class Item {
        String name;
        double price;
        int count;

        Item(String name, double price, int count) {
            this.name = name;
            this.price = price;
            this.count = count;
        }
    }
}