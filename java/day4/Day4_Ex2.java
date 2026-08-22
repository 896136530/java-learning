public class Day4_Ex2 {
    public static void main(String[] args) {
        // 题 2：写一个方法 getMax(int[] arr) 返回数组最大值
        int[] arr = { 85, 92, 78, 96, 60 };
        System.out.println("数组最大值：" + getMax(arr));
    }

    // ===== 你的代码写在这里（定义 getMax 方法，返回数组最大值）=====
    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    // ===========================================
}