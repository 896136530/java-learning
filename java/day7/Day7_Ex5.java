public class Day7_Ex5 {
    public static void main(String[] args) {
        // 题 5：Employee 父类 + Manager 子类——综合继承
        Manager m = new Manager("王总", 20000, 5000);
        System.out.println("总薪资：" + m.getSalary()); // 期望：总薪资：25000.0
        m.showInfo(); // 期望：员工：王总，薪资：20000.0元 / 奖金：5000.0元
    }
}

// ===== 你的代码写在这里（Employee 类 + Manager 类：super + 重写 getSalary/showInfo）=====
class Employee {
    protected String name;
    protected double salary;

    Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    double getSalary() {
        return salary;
    }

    void showInfo() {
        System.out.println("员工：" + name + "，薪资：" + salary + "元");
    }
}

class Manager extends Employee {
    double bonus;   // 奖金：只有经理有，放在子类

    Manager(String name, double salary, double bonus) {
        super(name, salary);   // 父类只认识 工资，奖金自己收
        this.bonus = bonus;
    }

    @Override
    double getSalary() {
        return super.getSalary() + bonus;   // 父类工资 + 自己奖金
    }

    @Override
    void showInfo() {
        super.showInfo();                    // 先打印父类的 员工+薪资
        System.out.println("奖金：" + bonus + "元");
    }
}
// ===========================================