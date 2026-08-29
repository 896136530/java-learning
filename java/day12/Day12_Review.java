import java.util.ArrayList;

public class Day12_Review {
    public static void main(String[] args) {
        // ======== ArrayList 全家桶完整示例：员工工资单 ========
        ArrayList<String> names = new ArrayList<>();
        names.add("小明");
        names.add("小红");
        names.add("小刚");

        ArrayList<Integer> salary = new ArrayList<>();
        salary.add(5000);
        salary.add(6000);
        salary.add(4500);

        System.out.println("员工数：" + names.size());         // 员工数：3
        System.out.println("第一个：" + names.get(0));        // 第一个：小明
        salary.set(2, 5000);                                  // 给小刚加工资
        System.out.println("小刚新工资：" + salary.get(2));   // 小刚新工资：5000
        names.add(1, "小丽");                                 // 指定位置插入
        System.out.println("插队后：" + names);               // 插队后：[小明, 小丽, 小红, 小刚]
        names.remove(1);                                      // 小丽离职，删掉
        System.out.println("删除后：" + names);               // 删除后：[小明, 小红, 小刚]
        System.out.println("含 小明？" + names.contains("小明")); // 含 小明？true
        System.out.println("小明下标：" + names.indexOf("小明")); // 小明下标：0

        System.out.println("---- 增强 for：只读遍历 ----");
        for (String n : names) {
            System.out.println(n);
        }

        System.out.println("---- 普通 for + set：工资翻倍 ----");
        for (int i = 0; i < salary.size(); i++) {
            salary.set(i, salary.get(i) * 2);
        }
        System.out.println(salary);                           // [10000, 12000, 10000]

        salary.clear();
        System.out.println("清空后 size=" + salary.size() + "，空？" + salary.isEmpty()); // 清空后 size=0，空？true
    }
}
// ===========================================