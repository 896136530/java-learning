public class Day7_Ex5 {
    public static void main(String[] args) {
        // 题 5：Employee 父类 + Manager 子类——综合继承
        Manager m = new Manager("王总", 20000, 5000);
        System.out.println("总薪资：" + m.getSalary());  // 期望：总薪资：25000.0
        m.showInfo();  // 期望：员工：王总，薪资：20000.0元 / 奖金：5000.0元
    }
}

// ===== 你的代码写在这里（Employee 类 + Manager 类：super + 重写 getSalary/showInfo）=====


// ===========================================