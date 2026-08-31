import java.util.HashSet;

public class Day14_Ex1 {
    public static void main(String[] args) {
        // 题 1：去重统计——HashSet 自动去重
        String[] names = {"小明", "小红", "小明", "小刚", "小红"};
        System.out.println("不重复人数：" + SetStats.uniqueCount(names));  // 期望：不重复人数：3
    }
}

// ===== 你的代码写在这里（类 SetStats：static int uniqueCount(String[] names)）=====

// ===========================================