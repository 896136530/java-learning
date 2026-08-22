import java.util.Scanner;

public class Day3_Ex2 {
    public static void main(String[] args) {
        // 题 2：输入数组长度和所有元素，输出最大值和平均值
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入数组长度：");
        int n = sc.nextInt();
        int[] arr = new int[n];
        System.out.print("请输入 " + n + " 个整数（空格分隔）：");
        for (int i = 0; i < n; i++) {
            arr[i] = sc.nextInt();
        }

        // ===== 你的代码写在这里（求最大值 max 和平均值 avg 并打印）=====
        // 提示：avg 要转 double，否则整数除法
        int max = arr[0];
        for (int i = 0; i < n; i++) {
            if (arr[i] >= max) {
                max = arr[i];
            }

        }
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum = sum + arr[i];
        }
        double avg = (double) sum / n;
        System.out.println("最大值为：" + max);
        System.out.println("平均值为：" + avg);
        // =============================================================
        sc.close();
    }
}
