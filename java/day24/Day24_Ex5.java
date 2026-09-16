import java.util.*;

public class Day24_Ex5 {
    public static void main(String[] args) {
        // 题 5：统计与筛选（把每一行 "姓名,分数" 解析出来）
        // ⚠️ 数据里最后有一个空行，必须跳过（否则 parseInt 会崩）
        List<String> lines = Arrays.asList("张三,88", "李四,95", "王五,76", "赵六,60", "陈七,45", "");

        System.out.println("总分：" + ScoreStat.total(lines));
        List<String> pass = ScoreStat.passOnly(lines);
        System.out.println("及格 " + pass.size() + " 人");
        for (String s : pass) {
            System.out.println(s);
        }
        // 期望输出：
        //   总分：364
        //   及格 4 人
        //   张三,88
        //   李四,95
        //   王五,76
        //   赵六,60
    }
}

// ===== 你的代码写在这里：class ScoreStat =====

// TODO：两个方法
//   ① static int total(List<String> lines)      → 所有分数的总和（空行跳过，不计数）
//   ② static List<String> passOnly(List<String> lines) → 返回分数 ≥ 60 的**原始行**
//   提示：
//     · line.split(",") 得到 ["张三","88"]，分数是 parts[1]，用 Integer.parseInt 转成 int
//     · 先判空行（line.trim().isEmpty()），再判 parts.length == 2，不合法就跳过
//     · 上面两段判断重复了，可以抽一个 private static String[] split(String line) 复用（返回 null 表示这行不合法）
// ===========================================
