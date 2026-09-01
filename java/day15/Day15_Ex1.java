import java.util.Queue;
import java.util.LinkedList;

public class Day15_Ex1 {
    public static void main(String[] args) {
        // 题 1：安全除法——除零别崩
        System.out.println("10/2=" + DivSafe.safeDivide(10, 2));  // 期望：10/2=5
        System.out.println("10/0=" + DivSafe.safeDivide(10, 0));  // 期望：10/0=-1（不崩！）
    }
}

// ===== 你的代码写在这里（类 DivSafe：static int safeDivide(int a, int b)，除零返回 -1）=====

// ===========================================