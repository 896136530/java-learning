package com.xiaoming.day37;

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
 * 一把秒表 + 一个执行计划阅读器（今天不用你写 Java，用它就行）。
 *
 * <pre>
 *   java -jar target\day37-bench.jar sql\02_基线.sql              ← 计时（默认每条跑 20 次，报最快/中位/平均）
 *   java -jar target\day37-bench.jar sql\02_基线.sql 50           ← 每条跑 50 次
 *   java -jar target\day37-bench.jar --explain sql\90_我的实验.sql   ← 打印完整 EXPLAIN（11 列全打）
 *   java -jar target\day37-bench.jar --analyze sql\90_我的实验.sql   ← 打印 EXPLAIN ANALYZE（估算 vs 真实）
 * </pre>
 *
 * 约定：.sql 文件里**一条语句以分号结尾**；注释**单独占一行**（以 -- 开头）。
 *
 * ⭐ 为什么必须用 Java 计时，而不是看 mysql 客户端的 "0.00 sec"？
 *   客户端只显示到小数点后两位，0.004 秒和 0.0004 秒都显示 0.00，看不出差别；
 *   这里用 System.nanoTime() 记到纳秒，并且**先预热 2 次**（第一次要读磁盘/建缓存，数字会虚高）。
 *
 * ⭐ 三个模式的分工（今天第 2~5 步都会用到）：
 *   计时   → 回答"快了多少"
 *   EXPLAIN        → 回答"走没走索引、估算读多少行"
 *   EXPLAIN ANALYZE→ 回答"**实际**读了多少行、每步花了几毫秒"（8.0.18 起才有）
 */
public class IndexBench {

    // ⭐ JDBC URL 里的 characterEncoding=UTF-8 是 Java 侧的字符集名（Day35 踩过：写 utf8mb4 会连不上）
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/day37"
            + "?useSSL=false&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String USER = "day37";
    private static final String PASSWORD = "day37";

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "-h".equals(args[0])) {
            usage();
            return;
        }
        String mode = "run";
        if ("--explain".equals(args[0])) {
            mode = "explain";
        } else if ("--analyze".equals(args[0])) {
            mode = "analyze";
        }
        int i = "run".equals(mode) ? 0 : 1;
        if (args.length <= i) {
            usage();
            return;
        }
        Path file = Path.of(args[i]);
        int repeat = args.length > i + 1 ? Integer.parseInt(args[i + 1]) : 20;
        List<String> sqls = readStatements(file);

        System.out.println("文件：" + file.toAbsolutePath());
        System.out.println("语句数：" + sqls.size() + switch (mode) {
            case "explain" -> "（EXPLAIN 模式）";
            case "analyze" -> "（EXPLAIN ANALYZE 模式）";
            default -> "（每条 " + repeat + " 次）";
        });
        System.out.println("库：" + URL);

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (int k = 0; k < sqls.size(); k++) {
                String sql = sqls.get(k);
                System.out.println();
                System.out.println("#" + (k + 1) + "  " + oneLine(sql));
                switch (mode) {
                    case "explain" -> explain(conn, sql);
                    case "analyze" -> analyze(conn, sql);
                    default -> run(conn, sql, repeat);
                }
            }
        }
    }

    /** 跑一条语句：查询计时（N 次），DDL / SET / ANALYZE 只跑 1 次 */
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

    /** 打印完整 EXPLAIN：11 列，按"表 → 用哪个索引 → 估算行数 → 附注"分三行，好读 */
    private static void explain(Connection conn, String sql) throws Exception {
        if (isSessionStmt(sql)) {
            // ⭐ SET optimizer_switch=... 这类"会话开关"必须真执行，否则后面几条 EXPLAIN
            //    看到的还是旧开关（第 5 步对比 ICP 开/关全靠它）
            execQuietly(conn, sql);
            System.out.println("    （会话设置已生效）");
            return;
        }
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            System.out.println("    跳过（EXPLAIN 只对 SELECT 有意义）");
            return;
        }
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("EXPLAIN " + sql)) {
            while (rs.next()) {
                System.out.printf("    id=%-3s select_type=%-12s table=%s%n",
                        nvl(rs.getString("id")),
                        nvl(rs.getString("select_type")),
                        nvl(rs.getString("table")));
                System.out.printf("    type=%-10s possible_keys=%-28s key=%-22s key_len=%-5s ref=%s%n",
                        nvl(rs.getString("type")),
                        nvl(rs.getString("possible_keys")),
                        nvl(rs.getString("key")),
                        nvl(rs.getString("key_len")),
                        nvl(rs.getString("ref")));
                System.out.printf("    rows=%-8s filtered=%-6s Extra=%s%n",
                        nvl(rs.getString("rows")),
                        nvl(rs.getString("filtered")),
                        nvl(rs.getString("Extra")));
            }
        }
    }

    /** 打印 EXPLAIN ANALYZE 的树：actual time / rows 是**真实**跑出来的，不是估算 */
    private static void analyze(Connection conn, String sql) throws Exception {
        if (isSessionStmt(sql)) {
            execQuietly(conn, sql);
            System.out.println("    （会话设置已生效）");
            return;
        }
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            System.out.println("    跳过（EXPLAIN ANALYZE 只对 SELECT 有意义）");
            return;
        }
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("EXPLAIN ANALYZE " + sql)) {
            while (rs.next()) {
                for (String line : rs.getString(1).split("\n")) {
                    System.out.println("    " + line);
                }
            }
        }
    }

    /** SET / USE 这类"会话设置"，在 EXPLAIN 模式下也要真执行 */
    private static boolean isSessionStmt(String sql) {
        String s = sql.trim().toUpperCase();
        return s.startsWith("SET ") || s.startsWith("USE ");
    }

    private static void execQuietly(Connection conn, String sql) throws Exception {
        try (Statement st = conn.createStatement()) {
            st.execute(sql);
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
        System.out.println("  java -jar target\\day37-bench.jar sql\\实验.sql [次数]   计时");
        System.out.println("  java -jar target\\day37-bench.jar --explain sql\\实验.sql  看执行计划（11 列）");
        System.out.println("  java -jar target\\day37-bench.jar --analyze sql\\实验.sql  看真实耗时（EXPLAIN ANALYZE）");
        System.out.println("约定：语句以分号结尾，注释单独一行（-- 开头）。");
    }
}