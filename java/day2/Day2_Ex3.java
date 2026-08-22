public class Day2_Ex3 {
    public static void main(String[] args) {
        // 题 3：九九乘法表（嵌套 for）
        // 期望输出：
        //   1*1=1
        //   1*2=2  2*2=4
        //   1*3=3  2*3=6  3*3=9
        //   ... 直到 9*9=81
        // 提示：外层 i 控制行（1~9），内层 j 控制列（1~i），对齐用 \t

        // ===== 你的代码写在这里 =====
for(int i=1; i<=9; i++){
    for(int j=1;j<=i;j++){
        System.out.print(j+"*"+i+"="+i*j +" ");
    }
    System.out.println();
        // ===========================
    }}
}
