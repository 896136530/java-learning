import java.util.*;

public class Day22_Ex2 {
    public static void main(String[] args) {
        // 题 2：ScoreBook 成绩本——新增、修改、查询
        ScoreBook book = new ScoreBook();
        book.addScore("张三", 88);
        book.updateScore("张三", 95);
        System.out.println("张三 当前成绩：" + book.getScore("张三"));   // 期望：张三 当前成绩：95
        Integer s2 = book.getScore("小明");
        System.out.println("小明 " + (s2 == null ? "不存在" : "成绩：" + s2));   // 期望：小明 不存在
    }
}

// ===== 你的代码写在这里：class ScoreBook =====

// TODO：ScoreBook 的三个方法
//   addScore(String name, int score)      → put
//   updateScore(String name, int score)   → put（相同 key 即覆盖）
//   Integer getScore(String name)         → 返回 map.get(name)（不存在返回 null，别用 int！）

// ===========================================