public class Day15_Ex2 {
    public static void main(String[] args) {
        // 题 2：安全转数字——转不了别崩
        System.out.println("42=" + NumParse.toInt("42")); // 期望：42=42
        System.out.println("abc=" + NumParse.toInt("abc")); // 期望：abc=0（不崩！）
    }
}

// ===== 你的代码写在这里（类 NumParse：static int toInt(String s)，转不了返回 0）=====
class NumParse {
    public static int toInt(String s) {
        try {
            int x = Integer.parseInt(s);
            return x;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
// ===========================================