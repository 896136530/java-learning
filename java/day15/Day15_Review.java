public class Day15_Review {
    public static void main(String[] args) {
        // ======== 异常演练场：四个典型场景全接住 ========
        System.out.println("10/0 = " + safeDiv(10, 0));          // -1，不崩
        System.out.println("'abc' = " + safeParse("abc"));       // 0，不崩
        String[] arr = {"a"};
        System.out.println("arr[9] = " + safeGet(arr, 9));       // <越界>，不崩
        System.out.println("finally 必定执行，顺序：");
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("  接住除零异常");
        } finally {
            System.out.println("  清理完成");
        }
        System.out.println("全部接住，程序完整跑完 ✅");
    }

    static int safeDiv(int a, int b) {
        try {
            return a / b;
        } catch (ArithmeticException e) {
            return -1;
        }
    }

    static int safeParse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    static String safeGet(String[] arr, int i) {
        try {
            return arr[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "<越界>";
        }
    }
}
// ===========================================