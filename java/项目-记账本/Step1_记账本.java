import java.util.HashMap;
import java.util.Scanner;

// ===========================================
// 项目 Step1：分类汇总（看《任务书.md》再做）
// TODO 只有两处：记一笔、查看汇总
// ===========================================
public class Step1_记账本 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, Integer> map = new HashMap<>();   // 分类 → 合计金额

        while (true) {
            System.out.println("======== 记账本 ========");
            System.out.println("1. 记一笔");
            System.out.println("2. 查看分类汇总");
            System.out.println("3. 退出");
            System.out.print("请选择：");
            int choice = Integer.parseInt(sc.nextLine());

            if (choice == 1) {
                System.out.print("分类（吃饭/交通/娱乐/收入）：");
                String cat = sc.nextLine();
                System.out.print("金额（整数，支出填负数）：");
                int amount = Integer.parseInt(sc.nextLine());

                // ===== Step1 TODO：把 (cat, amount) 记进 map（已存在就累加，不存在就新建）=====

                // ===========================================

            } else if (choice == 2) {
                // ===== Step1 TODO：遍历 map，每行打印「分类：金额」=====

                // ===========================================

            } else if (choice == 3) {
                System.out.println("再见！");
                break;
            } else {
                System.out.println("无效选择，请重新输入");
            }
        }
        sc.close();
    }
}