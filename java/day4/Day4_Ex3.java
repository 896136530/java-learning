public class Day4_Ex3 {
    public static void main(String[] args) {
        // 题 3：遍历 2~20，是质数就打印（期望：2 3 5 7 11 13 17 19）
        for (int i = 2; i <= 20; i++) {
            if (isPrime(i)) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }

    // ===== 你的代码写在这里（定义 isPrime 方法，判断 n 是否为质数并返回 boolean）=====
    public static boolean isPrime(int n) {
        if (n == 2) {
            return true;
        }
        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    // ===========================================
}
