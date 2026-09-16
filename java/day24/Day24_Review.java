import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Day24_Review {
    public static void main(String[] args) throws Exception {
        // 综合：成绩单导入导出（文件持久化 + Day23 的排行榜）
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("张三", 88);
        scores.put("李四", 95);
        scores.put("王五", 76);
        scores.put("赵六", 60);
        scores.put("钱七", 100);

        GradeBook.save("gradebook.txt", scores);
        System.out.println("已保存 " + scores.size() + " 人");

        Map<String, Integer> loaded = GradeBook.load("gradebook.txt");
        System.out.println("读回 " + loaded.size() + " 人");
        GradeBook.printTop3(loaded);
        System.out.println("平均分：" + GradeBook.average(loaded));
        // 期望输出：
        //   已保存 5 人
        //   读回 5 人
        //   第1名：钱七 100
        //   第2名：李四 95
        //   第3名：张三 88
        //   平均分：83.80
    }
}

// ===== 你的代码写在这里：class GradeBook =====

// TODO：四个方法
//   ① static void save(String path, Map<String,Integer> scores)
//        · 每行写成「姓名,分数」，用 entrySet 遍历；UTF-8；try-with-resources
//   ② static Map<String,Integer> load(String path)
//        · 读回来放进 **LinkedHashMap**（保持文件里的先后顺序，Day13 学过）
//        · 每行 split(",") → put(姓名, Integer.parseInt(分数))；空行跳过
//   ③ static void printTop3(Map<String,Integer> scores)      ← Day23 的排行榜，再来一遍
//        · entrySet 倒进 List → 按 value 降序（**同分按姓名升序兜底**）→ 打印前 3 名
//        · 格式：第1名：钱七 100（全角冒号！）
//        · 用 Math.min(3, list.size()) 防越界（数据不足 3 人时不崩）
//   ④ static String average(Map<String,Integer> scores)
//        · 先转 double 再除人数：String.format("%.2f", (double) sum / scores.size())
// ===========================================
