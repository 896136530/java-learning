import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

// ===========================================
// 项目 Step3：持久化——退出后再打开，账还在（看《任务书.md》再做）
// TODO 五处 A~E：
//    A = 启动时加载账本（读文件）
//    B = 记一笔（分类 + 金额 + 备注）
//    C = 删除一笔（按序号）
//    D = 查看总账（总收入 / 总支出 / 结余）
//    E = 退出时保存账本（写文件）
// 今日复习：Day17 IO 流（FileReader / FileWriter / BufferedReader）+ Day15 异常 + File 类
// ===========================================
public class Step3_记账本 {
    static final String FILE = "账本.txt";   // 数据文件（和 .java 同一个目录）

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Bill> bills = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();

        // ===== Step3 TODO A：启动时把账本读回来 =====
        // bill = new Bill(cat, amount, note);   bills.add(bill);
        // 读法：BufferedReader + while ((line = br.readLine()) != null) 逐行读
        //      每行格式是「分类<TAB>金额<TAB>备注」，用 line.split("\t") 拆开
        // 两个必须处理的坑：
        //   ① 文件不存在（第一次运行 / 被删了）→ 不能崩，当空账本
        //   ② 空行、拆出来不足 3 段的坏行 → 跳过（见提示区 D）
        // 读完不用自己 refresh 分类汇总——下面那行 refresh(bills, map) 会替你算
        // ===== 这里开始写你的加载代码 =====
        // ===========================================

        refresh(bills, map);   // 分类汇总：按明细重算（已写好，别改）

        while (true) {
            System.out.println("======== 记账本 v3 ========");
            System.out.println("1. 记一笔（分类 + 金额 + 备注）");
            System.out.println("2. 查看分类汇总");
            System.out.println("3. 查看明细（全部 / 按分类筛选）");
            System.out.println("4. 删除一笔");
            System.out.println("5. 查看总账（总收入 / 总支出 / 结余）");
            System.out.println("6. 退出并保存");
            System.out.print("请选择：");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("请输入数字！");
                continue;
            }

            if (choice == 1) {
                // ===== Step3 TODO B：记一笔（分类 + 金额 + 备注）=====
                // ① 读分类（提示：分类（吃饭/交通/娱乐/收入）：）
                // ② 读金额（支出填负数）——用你 Step2 写的"防崩循环"（try-catch 重试，输 abc 不崩）
                // ③ 读备注（提示：备注（一句话，比如 食堂午饭）：）
                // ④ bills.add(new Bill(cat, amount, note));   refresh(bills, map);
                // ⑤ 打印 已记一笔！
                // ===========================================

            } else if (choice == 2) {
                // 分类汇总（已写好，别改）
                if (map.isEmpty()) {
                    System.out.println("还没有账，先记一笔吧");
                } else {
                    for (String key : map.keySet()) {
                        System.out.println(key + "：" + map.get(key));
                    }
                }

            } else if (choice == 3) {
                // 查看明细：全部 + 可选按分类筛选（已写好，别改）
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
                // ===== Step3 TODO C：删除一笔（按序号）=====
                // ① 空账本 → 打印"还没有明细，先记一笔吧" + continue
                // ② 提示"请输入序号（1~" + bills.size() + "）："
                // ③ 读序号：一定要包 try-catch（输 abc 不能崩，参考 Step2 你改好的那份）
                // ④ 序号不在 1~bills.size() → "序号无效" + continue
                // ⑤ bills.remove(序号 - 1);  refresh(bills, map);  打印 已删除
                // ===========================================

            } else if (choice == 5) {
                // ===== Step3 TODO D：查看总账 =====
                // 总支出 = 所有负数金额相加（注意：累加的是 amount，负数加了自然变小）
                // 总收入 = 所有正数金额相加
                // 输出格式（三行）：
                //   总收入：100
                //   总支出：-20
                //   结余：80
                // 💡 支出想显示成 -20 就别取绝对值，直接加负数；
                //    结余 = 总收入 + 总支出（支出是负的，所以是加）
                // ===========================================

            } else if (choice == 6) {
                // ===== Step3 TODO E：退出时保存账本 =====
                // bills 为空 → 只打印 再见！ 然后 break（别把空账本覆盖掉老文件）
                // 否则：先 saveBills(bills) 保存，再打印 已保存 N 笔账，再见！
                // 💡 顺序很重要：保存完再 break 退出
                // ===========================================
                System.out.println("再见！");
                break;   // ← 先给个"裸退出"让模板能编译；你写完上面的保存逻辑后，把这两行按需调整

            } else {
                System.out.println("无效选择，请重新输入");
            }
        }
        sc.close();
    }

    /** 分类汇总：按明细重算（已写好，别改）——保证删除后汇总也跟着变 */
    static void refresh(ArrayList<Bill> bills, HashMap<String, Integer> map) {
        map.clear();
        for (Bill b : bills) {
            if (map.containsKey(b.cat)) {
                map.put(b.cat, map.get(b.cat) + b.amount);
            } else {
                map.put(b.cat, b.amount);
            }
        }
    }

    // ===== 下面两个方法给你用（已写好，别改）=====

    /** 保存明细到 账本.txt：每行「分类<TAB>金额<TAB>备注」 */
    static void saveBills(ArrayList<Bill> bills) throws IOException {
        try (FileWriter fw = new FileWriter(FILE)) {
            for (Bill b : bills) {
                fw.write(b.cat + "\t" + b.amount + "\t" + b.note + "\n");
            }
        }
    }

    /** 打印账本里有几笔（调试用） */
    static void printCount(ArrayList<Bill> bills) {
        System.out.println("当前明细：" + bills.size() + " 笔");
    }

    // ===== 提示区（先读一遍，能省你 20 分钟）=====
    // D-1 读文件三件套：
    //     BufferedReader br = new BufferedReader(new FileReader(FILE));
    //     String line;
    //     while ((line = br.readLine()) != null) { ... }
    //     br.close();   ← 别忘了关
    //
    // D-2 文件不存在怎么办：FileReader 找不到文件会抛 FileNotFoundException
    //     → 两种写法任选：
    //        写法一（推荐，一眼看懂）：if (!new File(FILE).exists()) { /* 当空账本，什么都不做 */ }
    //        写法二（try-catch）：catch (IOException e) { /* 当空账本 */ }
    //     ⚠️ main 上是 throws Exception，所以"不接住"也能编译过——但那样第一次运行程序就崩，
    //        你 Step2 辛苦练的"防崩"就白费了，这题必须接住。
    //
    // D-3 一行怎么拆：
    //     String[] parts = line.split("\t");
    //     if (parts.length < 3) continue;          // 坏行/空行跳过
    //     String cat = parts[0];
    //     int amount = Integer.parseInt(parts[1]); // 这行也可能坏，想稳就包 try-catch
    //     String note = parts[2];
    //
    // D-4 \t 是什么：制表符（Tab 键打出来的那个）。
    //     用 Tab 而不是空格当分隔符，是因为**备注里可能有空格**（"食堂 午饭"）——
    //     用空格分隔就会把一句备注拆成两段。这是真实项目里选分隔符的经典考虑。
    // ===========================================
}

// ===== Step3：Bill 类（沿用 Step2 你的版本）=====
class Bill {
    String cat;
    int amount;
    String note;

    Bill(String cat, int amount, String note) {
        this.cat = cat;
        this.amount = amount;
        this.note = note;
    }
}