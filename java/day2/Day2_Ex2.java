public class Day2_Ex2 {
    public static void main(String[] args) {
        // 题 2：打印 1~100 之间所有 3 的倍数，用空格分隔
        // 挑战：每打印 5 个换一行（提示：用一个计数器，% 5 == 0 时换行）
        // 提示：for (int i = 1; i <= 100; i++) + if (i % 3 == 0)

        // ===== 你的代码写在这里 =====
        int count=0;
for(int i=1;i<=100;i++){
    if(i%3==0){
        System.out.print(i+" ");
        count++;
    
    if(count%5==0){
        System.out.println();
    }
}}

        // ===========================
    }
}
