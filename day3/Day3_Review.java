import java.util.Random;
import java.util.Scanner;

/**
 * Day 3 综合完整示例（复习用，不需要写代码）
 *
 * 用途：做完 Day3_Ex1 ~ Day3_Ex5 后，直接运行本文件，
 * 把今天学的所有知识点完整过一遍：
 *   数组创建 → 遍历 → 最大值 → 求和平均 → 反转 → Scanner 输入 → Random 随机数
 *
 * 运行：VS Code 打开本文件 → 右上角 ▶ Run
 * （本示例的 Scanner 从字符串读数据，所以不会卡住等待输入；
 *   平时自己写代码用 new Scanner(System.in) 才是从键盘读。）
 */
public class Day3_Review {
    public static void main(String[] args) {
        // 1. 数组创建与遍历（for-each）
        int[] arr = {85, 92, 78, 96, 60};
        System.out.print("数组：");
        for (int x : arr) {
            System.out.print(x + " ");
        }
        System.out.println();

        // 2. 求最大值
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        System.out.println("最大值：" + max);

        // 3. 求和 + 平均
        int sum = 0;
        for (int x : arr) sum += x;
        System.out.println("和：" + sum + "，平均值：" + (double) sum / arr.length);

        // 4. 原地反转
        for (int i = 0; i < arr.length / 2; i++) {
            int tmp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = tmp;
        }
        System.out.print("反转后：");
        for (int x : arr) {
            System.out.print(x + " ");
        }
        System.out.println();

        // 5. Scanner 输入演示（从字符串读，不阻塞）
        //    平时用：Scanner sc = new Scanner(System.in);
        Scanner sc = new Scanner("5 10 20 30 40 50");
        int n = sc.nextInt();
        System.out.println("Scanner 读到的第一个数（作为长度）：" + n);
        int[] b = new int[n];
        for (int i = 0; i < n; i++) {
            b[i] = sc.nextInt();
        }
        System.out.print("Scanner 读入的数组：");
        for (int x : b) {
            System.out.print(x + " ");
        }
        System.out.println();

        // 6. Random 随机数
        Random rand = new Random();
        System.out.println("随机数（1~100）：" + (rand.nextInt(100) + 1));
    }
}
