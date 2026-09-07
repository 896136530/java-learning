import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// ===========================================
// 项目 Step2：明细留档 + 防崩（看《任务书.md》再做）
// TODO 四处 A~D：
//    A = Bill 类（三个字段 + 构造器）
//    B = 金额输入防崩循环（try-catch 重试）
//    C = 记一笔：new Bill(...) 加入 bills 列表
//    D = 删除一笔（按序号）
// 今日复习：Day5 类与对象 + Day12 ArrayList + Day15 异常 try-catch
// ===========================================
public class Step2_记账本 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        HashMap<String, Integer> map = new HashMap<>();   // 分类 → 合计（沿用 Step1 的招式）
        ArrayList<Bill> bills = new ArrayList<>();        // 明细留档（Step2 新增）

        while (true) {
            System.out.println("======== 记账本 v2 ========");
            System.out.println("1. 记一笔（分类 + 金额 + 备注）");
            System.out.println("2. 查看分类汇总");
            System.out.println("3. 查看明细（全部 / 按分类筛选）");
            System.out.println("4. 删除一笔");
            System.out.println("5. 退出");
            System.out.print("请选择：");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("请输入数字！");   // 选项输错也不崩
                continue;
            }

            if (choice == 1) {
                System.out.print("分类（吃饭/交通/娱乐/收入）：");
                String cat = sc.nextLine();

                // ===== Step2 TODO B：金额输入防崩循环 =====
                // 目标：while(true) 里 try { amount = Integer.parseInt(sc.nextLine()); break; }
                //       catch (NumberFormatException) { 提示"金额必须是整数，请重新输入" }
                // 支出填负数（比如 -12）
                int amount = 0;
                // ===== 这里开始写你的防崩循环 =====
                // ===========================================

                System.out.print("备注（一句话，比如 食堂午饭）：");
                String note = sc.nextLine();

                // 分类汇总累加（Step1 你的招式：已存在就累加，不存在就新建）
                if (map.containsKey(cat)) {
                    map.put(cat, map.get(cat) + amount);
                } else {
                    map.put(cat, amount);
                }

                // ===== Step2 TODO C：把这一笔包装成 Bill 对象，加入 bills 列表 =====
                // bill = new Bill(cat, amount, note);  bills.add(bill);
                // ===========================================
                System.out.println("已记一笔！");

            } else if (choice == 2) {
                // 分类汇总（Step1 招式）
                for (String key : map.keySet()) {
                    System.out.println(key + "：" + map.get(key));
                }

            } else if (choice == 3) {
                // 查看明细：全部 + 可选按分类筛选（带序号，从 1 开始）
                if (bills.isEmpty()) {
                    System.out.println("还没有明细，先记一笔吧");
                    continue;
                }
                for (int i = 0; i < bills.size(); i++) {
                    Bill b = bills.get(i);
                    System.out.println((i + 1) + ". " + b.cat + " " + b.amount + " " + b.note);
                }
                System.out.print("按分类筛选请输入分类名，直接回车查看全部：");
                String filter = sc.nextLine();
                if (!filter.isEmpty()) {
                    System.out.println("===== 「" + filter + "」的明细 =====");
                    for (int i = 0; i < bills.size(); i++) {
                        Bill b = bills.get(i);
                        if (b.cat.equals(filter)) {
                            System.out.println((i + 1) + ". " + b.cat + " " + b.amount + " " + b.note);
                        }
                    }
                }

            } else if (choice == 4) {
                // ===== Step2 TODO D：删除一笔 =====
                // 提示：如果 bills 是空的，打印"还没有明细"并 continue（参考选项 3 的写法）
                // 然后提示输入序号（就是选项 3 打出来的那个编号），
                // 序号非法（不在 1~bills.size() 之间）提示"序号无效"并 continue，
                // 合法就 bills.remove(序号-1)，打印"已删除"
                // ===========================================

            } else if (choice == 5) {
                System.out.println("再见！");
                break;
            } else {
                System.out.println("无效选择，请重新输入");
            }
        }
        sc.close();
    }
}

// ===== Step2 TODO A：Bill 类 =====
// 三个公开字段：String cat（分类）、int amount（金额）、String note（备注）
// 一个构造器：Bill(String cat, int amount, String note) 把三个参数赋给字段
// ===== 这里开始写 Bill 类 =====
// ===========================================