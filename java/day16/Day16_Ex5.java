public class Day16_Ex5 {
    public static void main(String[] args) {
        // 题 5：泛型方法 swap——交换数组两个位置
        Integer[] nums = {1, 2, 3};
        Swapper.swap(nums, 0, 2);   // 交换下标 0 和 2
        for (int i = 0; i < nums.length; i++) {
            if (i > 0) {
                System.out.print(" ");
            }
            System.out.print(nums[i]);
        }
        System.out.println();       // 期望：3 2 1
    }
}

// ===== 你的代码写在这里（类 Swapper：static <T> void swap(T[] arr, int i, int j)）=====
class Swapper{
    public static <T> void swap(T[] arr,int i,int j){
        T temp=arr[i];
        arr[i]=arr[j];
        arr[j]=temp;
    }
}
// ===========================================