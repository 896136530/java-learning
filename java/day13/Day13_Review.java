import java.util.HashMap;

public class Day13_Review {
    public static void main(String[] args) {
        // ======== HashMap 全家桶完整示例：班级通讯录 ========
        HashMap<String, String> phone = new HashMap<>();
        phone.put("小明", "13800138001");
        phone.put("小红", "13800138002");
        phone.put("小刚", "13800138003");

        System.out.println("通讯录人数：" + phone.size());            // 通讯录人数：3
        System.out.println("小明的电话：" + phone.get("小明"));       // 小明的电话：13800138001
        phone.put("小明", "13999999999");                            // 换号 = put 覆盖
        System.out.println("换号后：" + phone.get("小明"));           // 换号后：13999999999
        System.out.println("有小丽吗？" + phone.containsKey("小丽")); // 有小丽吗？false
        phone.put("小丽", "13700137000");                            // 新同学入册
        System.out.println("新增后人数：" + phone.size());            // 新增后人数：4
        String removed = phone.remove("小刚");                       // 转学删除，返回被删的值
        System.out.println("删掉的号码：" + removed);                // 删掉的号码：13800138003

        System.out.println("---- keySet 遍历：挨个报号码 ----");
        for (String name : phone.keySet()) {
            System.out.println(name + "：" + phone.get(name));
        }

        System.out.println("---- getOrDefault：查不到的给默认值 ----");
        System.out.println("小刚(已删)：" + phone.getOrDefault("小刚", "查无此人"));  // 查无此人
        System.out.println("小丽：" + phone.getOrDefault("小丽", "查无此人"));        // 13700137000
    }
}
// ===========================================