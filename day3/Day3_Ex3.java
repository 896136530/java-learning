public class Day3_Ex3 {
    public static void main(String[] args) {
        // 题 3：把数组原地反转（不新建数组），最终打印 6 5 4 3 2 1
        int[] arr = {1, 2, 3, 4, 5, 6};
        // 提示：arr[i] 和 arr[arr.length - 1 - i] 交换，i 走到 length/2 即可

        // ===== 你的代码写在这里 =====
for(int i=0;i<=arr.length/2-1;i++){
    int temp=0;
    temp=arr[i];
    arr[i]=arr[arr.length-1-i];
    arr[arr.length-1-i]=temp;
}
        // ===========================

        // 打印结果（应输出 6 5 4 3 2 1）
        for (int x : arr) {
            System.out.print(x + " ");
        }
    }
}
