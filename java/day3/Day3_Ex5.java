import java.util.Scanner;

public class Day3_Ex5 {
    public static void main(String[] args) {
        // 题 5：输入 5 个成绩，统计及格（>=60）和不及格的人数

        // ===== 你的代码写在这里（用数组存 5 个成绩，再遍历统计）=====
        // 输出示例："及格人数：3，不及格人数：2"
        Scanner sc = new Scanner(System.in);
        int score[] = new int[5];
        for (int i = 0; i < 5; i++) {
            score[i] = sc.nextInt();
        }
        int x = 0;
        int y = 0;
        for (int i = 0; i < 5; i++) {
            if (score[i] >= 60) {
                x++;
            } else {
                y++;
            }
        }
        System.out.println("及格人数：" + x + ",不及格人数：" + y);
        // ===========================================================
        sc.close();
    }
}
