/**
 * Day 4 综合完整示例（复习用，不需要写代码）
 *
 * 用途：做完 Day4_Ex1 ~ Day4_Ex5 后，直接运行本文件，
 * 把今天学的所有知识点完整过一遍：
 *   方法定义与调用 → 参数与返回值 → return → 方法重载 → 值传递
 *
 * 运行：VS Code 打开本文件 → 右上角 ▶ Run
 */
public class Day4_Review {
    public static void main(String[] args) {
        // 1. 无参数无返回值的方法
        printLine();

        // 2. 有参数、有返回值
        int sum = add(3, 5);
        System.out.println("add(3, 5) = " + sum);

        // 3. 数组作为参数
        int[] scores = {85, 92, 78, 96, 60};
        System.out.println("数组最大值：" + getMax(scores));
        System.out.println("数组平均值：" + getAvg(scores));

        // 4. 方法重载
        System.out.println("正方形面积：" + area(5));
        System.out.println("长方形面积：" + area(4, 6));
        System.out.println("圆的面积：" + area(3.0));

        // 5. 值传递演示（基本类型 vs 数组）
        int num = 10;
        int[] arr = {10, 20, 30};
        changeNum(num);
        changeArr(arr);
        System.out.println("基本类型 num 调用后（不变）：" + num);
        System.out.println("数组 arr[0] 调用后（变了）：" + arr[0]);

        // 6. return 直接结束方法：打印 2~20 的质数
        System.out.print("2~20 的质数：");
        for (int i = 2; i <= 20; i++) {
            if (isPrime(i)) System.out.print(i + " ");
        }
        System.out.println();
    }

    // 无参数无返回值
    public static void printLine() {
        System.out.println("====================");
    }

    // 有参数有返回值
    public static int add(int a, int b) {
        return a + b;
    }

    // 数组作参数：求最大值
    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        return max;
    }

    // 数组作参数：求平均值
    public static double getAvg(int[] arr) {
        int sum = 0;
        for (int x : arr) sum += x;
        return (double) sum / arr.length;
    }

    // 方法重载：同名不同参数
    public static int area(int side) { return side * side; }
    public static int area(int w, int h) { return w * h; }
    public static double area(double r) { return 3.14 * r * r; }

    // 基本类型：传值（副本），不影响外面
    public static void changeNum(int x) { x = 999; }

    // 数组：传引用（地址），修改会影响外面
    public static void changeArr(int[] a) { a[0] = 999; }

    // 质数判断
    public static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= n / 2; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}