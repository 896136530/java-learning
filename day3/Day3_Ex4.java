import java.util.Random;
import java.util.Scanner;

public class Day3_Ex4 {
    public static void main(String[] args) {
        // 题 4：🎮 猜数字游戏
        // 1. Random 生成 1~100 的随机数
        // 2. 用户最多猜 10 次，每次提示"大了"或"小了"
        // 3. 猜中输出"恭喜！第 X 次猜中"，10 次没猜中输出正确答案
        Random rand = new Random();
        int target = rand.nextInt(100) + 1; // 1~100
        Scanner sc = new Scanner(System.in);
        System.out.println("猜一个 1~100 的数字，你只有 10 次机会！");

        // ===== 你的代码写在这里 =====
        // 提示：for (int i = 1; i <= 10; i++) { 读入猜测 → 比较 → 提示 }
        // 猜中可以用 return 直接结束
        for (int i = 0; i < 10; i++) {
            int n = sc.nextInt();
            if (n > target) {
                System.out.println("大了");
            } else if (n < target) {
                System.out.println("小了");
            } else if (n == target) {
                System.out.println("恭喜！第" + (i + 1) + "次猜中");
                return;
            }

        }
        System.out.println("游戏结束，正确答案是：" + target);

        // ===========================
        sc.close();
    }
}
