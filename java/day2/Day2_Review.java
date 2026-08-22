/**
 * Day 2 综合完整示例（复习用，不需要写代码）
 *
 * 用途：做完 Day2_Ex1 ~ Day2_Ex5 后，直接运行本文件，
 * 把今天学的所有知识点完整过一遍：
 *   运算符 → if-else 闰年 → switch 等级 → for 求和 → while+continue → do-while → break
 *
 * 运行：VS Code 打开本文件 → 右上角 ▶ Run
 */
public class Day2_Review {
    public static void main(String[] args) {
        // 1. 运算符
        int a = 10, b = 3;
        System.out.println("10/3=" + (a / b) + "  10%3=" + (a % b));   // 3, 1
        int i = 5;
        System.out.println("i++=" + i++ + "（先用后加）现在 i=" + i);    // 5, 6
        int j = 5;
        System.out.println("++j=" + (++j) + "（先加后用）");             // 6
        System.out.println("三元: " + (a > b ? "a 大" : "b 大"));       // a 大

        // 2. if-else：闰年
        int year = 2024;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            System.out.println(year + " 是闰年");
        } else {
            System.out.println(year + " 不是闰年");
        }

        // 3. switch：成绩等级（箭头写法，JDK14+）
        int score = 85;
        String grade = switch (score / 10) {
            case 10, 9 -> "A";
            case 8     -> "B";
            case 7     -> "C";
            case 6     -> "D";
            default    -> "E";
        };
        System.out.println(score + " 分 -> " + grade);

        // 4. for：1+2+...+100
        int sum = 0;
        for (int n = 1; n <= 100; n++) {
            sum += n;
        }
        System.out.println("for: 1~100 和 = " + sum);   // 5050

        // 5. while + continue：打印 1~20 的奇数
        int n = 0;
        while (n < 20) {
            n++;
            if (n % 2 == 0) continue;   // 偶数跳过
            System.out.print(n + " ");
        }
        System.out.println("（奇数）");

        // 6. do-while：1+...+10（至少执行一次）
        int k = 1, total = 0;
        do {
            total += k;
            k++;
        } while (k <= 10);
        System.out.println("do-while: 1~10 和 = " + total);   // 55

        // 7. break：找第一个能被 7 整除的数
        for (int x = 1; ; x++) {        // 无限循环，靠 break 退出
            if (x % 7 == 0) {
                System.out.println("第一个能被 7 整除的数: " + x);   // 7
                break;
            }
        }
    }
}
