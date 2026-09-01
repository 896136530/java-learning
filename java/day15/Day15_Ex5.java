public class Day15_Ex5 {
    public static void main(String[] args) {
        // 题 5：容错累加——坏数据跳过，别崩
        String[] tokens = {"1", "2", "x", "3"};
        System.out.println("总和：" + SafeSum.sumTo(tokens));  // 期望：总和：6
    }
}

// ===== 你的代码写在这里（类 SafeSum：static int sumTo(String[] tokens)）=====

// ===========================================