public class Day15_Ex4 {
    public static void main(String[] args) {
        // 题 4：多异常分流——一个 try 两个 catch
        int[] nums = {1, 2, 3};
        System.out.println(Describe.describe(nums, 1));  // 期望：值=2
        System.out.println(Describe.describe(nums, 5));  // 期望：越界
        System.out.println(Describe.describe(null, 1));  // 期望：数组为null
    }
}

// ===== 你的代码写在这里（类 Describe：static String describe(int[] arr, int i)）=====

// ===========================================