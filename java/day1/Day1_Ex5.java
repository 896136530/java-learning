public class Day1_Ex5 {
    public static void main(String[] args) {
        // 题 5：溢出实验 + 思考
        // 实验 1：int x = 9000000000; 会编译报错，试试，再把类型改成 long 加 L 后缀
        // 实验 2：int y = 2000000000 * 4; 预测 y 是多少，打印出来看
        // 思考：为什么 int 存不下 90 亿？long 为什么可以？（答案写在注释里）

        // ===== 你的代码写在这里 =====
//int x=9000000000;
long y=9000000000L;
int m = 2000000000 * 4;
System.out.println("y=" + y);
System.out.println("m=" + m);
        // ===========================
    }
}
