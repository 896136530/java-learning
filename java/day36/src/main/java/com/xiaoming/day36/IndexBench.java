package com.xiaoming.day36;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一把秒表（今天不用你写 Java，用它就行）。
 *
 * <pre>
 *   java -jar target\day36-index.jar sql\02_基线.sql          ← 计时（默认每条跑 20 次，取最快/中位/平均）
 *   java -jar target\day36-index.jar sql\02_基线.sql 50       ← 每条跑 50 次
 *   java -jar target\day36-index.jar --explain sql\90_我的实验.sql  ← 打印 EXPLAIN（看 type / key / key_len / rows）
 * </pre>
 *
 * 约定：.sql 文件里**一条语句以分号结尾**；注释**单独占一行**（以 -- 开头）。
 *
 * ⭐ 为什么必须用 Java 计时，而不是看 mysql 客户端的 "0.00 sec"？
 *   客户端只显示到小数点后两位，0.004 秒和 0.0004 秒都显示 0.00，看不出差别；
 *   这里用 System.nanoTime() 记到纳秒，并且**先预热 2 次**（第一次要读磁盘/建缓存，数字会虚高）。
 */
public class IndexBench {

    // ⭐ JDBC URL 里的 characterEncoding=UTF-8 是 Java 侧的字符集名（Day35 踩过：写 utf8mb4 会连不上）
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/day36"
            + "?useSSL=false&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "day36";
    private static final String PASSWORD = "day36";

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "-h".equals(args[0])) {
            usage();
            return;
        }
        boolean explain = "--explain".equals(args[0]);
        int i = explain ? 1 : 0;
        if (args.length <= i) {
            usage();
            return;
        }
        Path file = Path.of(args[i]);
        int repeat = args.length > i + 1 ? Integer.parseInt(args[i + 1]) : 20;
        List<String> sqls = readStatements(file);

        System.out.println("文件：" + file.toAbsolutePath());
        System.out.println("语句数：" + sqls.size() + (explain ? "（EXPLAIN 模式）" : "（每条 " + repeat + " 次）"));
        System.out.println("库：" + URL);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (int k = 0; k < sqls.size(); k++) {
                String sql = sqls.get(k);
                System.out.println();
                System.out.println("#" + (k + 1) + "  " + oneLine(sql));
                if (explain) {
                    explain(conn, sql);
                } else {
                    run(conn, sql, repeat);
                }
            }
        }
    }

    /** 跑一条语句：查询计时（N 次），DDL/ALTER 只跑 1 次 */
    private static void run(Connection conn, String sql, int repeat) throws Exception {
        try (Statement st = conn.createStatement()) {
            long t0 = System.nanoTime();
            boolean hasResultSet = st.execute(sql);          // true = 有结果集（查询），false = 影响行数
            if (!hasResultSet) {
                long ms = (System.nanoTime() - t0) / 1_000_000;
                System.out.printf("    非查询语句：执行 1 次，耗时 %d ms（影响 %d 行）%n", ms, st.getUpdateCount());
                return;
            }
            int rows = drain(st.getResultSet());
            for (int w = 0; w < 2; w++) {                     // 预热，丢弃
                rows = drain(st.executeQuery(sql));
            }
            long[] cost = new long[repeat];
            for (int r = 0; r < repeat; r++) {
                long s = System.nanoTime();
                rows = drain(st.executeQuery(sql));
                cost[r] = System.nanoTime() - s;
            }
            Arrays.sort(cost);
            long sum = 0;
            for (long c : cost) {
                sum += c;
            }
            System.out.printf("    行数=%-7d 最快 %.2f ms | 中位 %.2f ms | 平均 %.2f ms%n",
                    rows,
                    cost[0] / 1_000_000.0,
                    cost[cost.length / 2] / 1_000_000.0,
                    sum / 1_000_000.0 / repeat);
        }
    }

    /** 打印 EXPLAIN：只挑今天要看的那几列 */
    private static void explain(Connection conn, String sql) throws Exception {
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            System.out.println("    跳过（EXPLAIN 只对 SELECT 有意义）");
            return;
        }
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("EXPLAIN " + sql)) {
            while (rs.next()) {
                System.out.printf("    table=%-8s type=%-8s key=%-22s key_len=%-6s rows=%-8s filtered=%-6s Extra=%s%n",
                        nvl(rs.getString("table")),
                        nvl(rs.getString("type")),
                        nvl(rs.getString("key")),
                        nvl(rs.getString("key_len")),
                        nvl(rs.getString("rows")),
                        nvl(rs.getString("filtered")),
                        nvl(rs.getString("Extra")));
            }
        }
    }

    /** 把结果集读完（不读完 = 没真正付 IO 的代价，计时会偏小） */
    private static int drain(ResultSet rs) throws Exception {
        int n = 0;
        while (rs.next()) {
            n++;
        }
        rs.close();
        return n;
    }

    /** 读文件 → 去掉整行注释 → 按分号切 → 去空白 */
    private static List<String> readStatements(Path file) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("--")) {
                continue;
            }
            sb.append(line).append('\n');
        }
        List<String> list = new ArrayList<>();
        for (String part : sb.toString().split(";")) {
            String s = part.trim();
            if (!s.isEmpty()) {
                list.add(s);
            }
        }
        return list;
    }

    private static String oneLine(String sql) {
        String s = sql.replaceAll("\\s+", " ").trim();
        return s.length() > 110 ? s.substring(0, 110) + "…" : s;
    }

    private static String nvl(String s) {
        return s == null ? "-" : s;
    }

    private static void usage() {
        System.out.println("用法：");
        System.out.println("  java -jar target\\day36-index.jar sql\\实验.sql [次数]   计时");
        System.out.println("  java -jar target\\day36-index.jar --explain sql\\实验.sql  看执行计划");
        System.out.println("约定：语句以分号结尾，注释单独一行（-- 开头）。");
    }
}
