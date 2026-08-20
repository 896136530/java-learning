public class Day1_Ex4 {
    public static void main(String[] args) {
        // 题 4：交换 a 和 b 的值
        // 第一步：借助临时变量；第二步（挑战）：不借助临时变量，用加减法
        int a = 3, b = 7;
        System.out.println("交换前 a=" + a + " b=" + b);

        // ===== 你的代码写在这里 =====
//int temp =a;
//a=b;
//b=temp;
a=a+b;
b=a-b;
a=a-b;



        // ===========================

        System.out.println("交换后 a=" + a + " b=" + b);
    }
}
