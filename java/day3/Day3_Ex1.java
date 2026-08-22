import java.util.Scanner;

public class Day3_Ex1 {
    public static void main(String[] args) {
        // 题 1：用 Scanner 输入两个整数，输出较大的那个
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入第一个整数：");
        int a = sc.nextInt();
        System.out.print("请输入第二个整数：");
        int b = sc.nextInt();

        // ===== 你的代码写在这里（if 比较并输出）=====
        if (a > b) {

            System.out.println("较大的整数是：" + a);
        } else if(a<b){

            System.out.println("较大的整数是：" + b);
        }
        else{
            System.out.println("两个整数相等：" + a + "和" + b);        
        }
        // ===========================================
        sc.close();
    }
}
