/**
 * Day 1 综合完整示例（复习用，不需要写代码）
 *
 * 用途：做完 Day1_Ex1 ~ Day1_Ex5 后，直接运行本文件，
 * 把今天学的所有知识点完整过一遍：
 *   8 种基本类型 → 类型转换 → 运算的坑 → 圆面积 → 交换变量 → 常量
 *
 * 运行：VS Code 打开本文件 → 右上角 ▶ Run
 */
public class Day1_Review {
    public static void main(String[] args) {
        // 1. 8 种基本类型
        byte b = 100;
        short s = 30000;
        int age = 20;
        long big = 9000000000L;   // long 要加 L
        float f = 3.14f;          // float 要加 f
        double pi = 3.1415926;
        char ch = 'A';            // 单引号
        boolean ok = true;
        System.out.println("byte=" + b + " short=" + s + " int=" + age + " long=" + big);
        System.out.println("float=" + f + " double=" + pi + " char=" + ch + " boolean=" + ok);

        // 2. 类型转换
        int i = 100;
        long l = i;              // 自动提升：小 -> 大
        int back = (int) 3.99;   // 强制转换：大 -> 小（截断，不四舍五入）
        System.out.println("自动提升 long=" + l + "  (int)3.99=" + back);

        // 3. 运算的坑
        System.out.println("7 / 2   = " + (7 / 2));    // 3   整数除法截断
        System.out.println("7.0 / 2 = " + (7.0 / 2));  // 3.5 有小数参与就是小数除法
        System.out.println("7 % 2   = " + (7 % 2));    // 1   取余数

        // 4. 圆面积（半径 5.5）
        double r = 5.5;
        double area = 3.1415926 * r * r;
        System.out.println("半径 " + r + " 的圆面积 = " + area);

        // 5. 交换变量（临时变量法）
        int a = 3, c = 7;
        System.out.println("交换前 a=" + a + " c=" + c);
        int tmp = a;
        a = c;
        c = tmp;
        System.out.println("交换后 a=" + a + " c=" + c);

        // 6. 常量
        final double PI = 3.14159265358979;
        System.out.println("常量 PI = " + PI);
    }
}
