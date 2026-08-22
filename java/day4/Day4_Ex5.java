public class Day4_Ex5 {
    public static void main(String[] args) {
        // 题 5：验证"值传递"——基本类型 vs 数组
        int num = 10;
        int[] arr = { 10, 20, 30 };

        changeNum(num);
        changeArr(arr);

        System.out.println("num 现在是：" + num); // 期望还是 10
        System.out.println("arr[0] 现在是：" + arr[0]); // 期望变成 999，先想想为什么
    }

    // ===== 你的代码写在这里（定义 changeNum(int x) 和 changeArr(int[] arr) 方法）=====
    public static void changeNum(int x) {
        x = 999;
    }

    public static void changeArr(int[] arr) {
        arr[0] = 999;
    }

    // ===========================================
}