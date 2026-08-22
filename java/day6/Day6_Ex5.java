public class Day6_Ex5 {
    public static void main(String[] args) {
        // 题 5：Employee 综合封装——setSalary 校验 + raise 加薪
        Employee e = new Employee(1, "张三", 8000);
        e.setSalary(-100);   // 期望：薪资不合法：-100.0（拒绝）
        e.raise(500);        // 8000 → 8500
        e.setSalary(9000);   // 8500 → 9000（合法，覆盖）
        System.out.println("员工：" + e.getId() + "  " + e.getName() + "  " + e.getSalary());
        // 期望：员工：1  张三  9000.0
    }
}

// ===== 你的代码写在这里（Employee 类：private id/name/salary + 构造方法 + getter + setSalary 校验 + raise）=====


// ===========================================