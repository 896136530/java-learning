import java.util.*;

public class Day23_Ex3 {
    public static void main(String[] args) {
        // 题 3：HashMap 按姓名查成绩（查表）
        ScoreTable table = new ScoreTable();
        table.put("张三", 88);
        table.put("李四", 95);
        table.put("王五", 76);

        Integer s = table.get("李四");
        System.out.println("李四 " + s);                                        // 期望：李四 95
        Integer missing = table.get("赵六");
        System.out.println("赵六 " + (missing == null ? "无成绩" : missing));    // 期望：赵六 无成绩
        System.out.println("在册人数：" + table.size());                         // 期望：在册人数：3
    }
}

// ===== 你的代码写在这里：class ScoreTable =====

// TODO：ScoreTable 类
//   内部字段：Map<String, Integer> map = new HashMap<>();
//   void put(String name, int score)  → 存/改（put 同 key 即覆盖）
//   Integer get(String name)          → 查，查不到返回 null（返回类型必须是 Integer！）
//   int size()                        → 在册人数

// ===========================================
