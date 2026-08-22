/**
 * Day 5 综合完整示例（复习用，不需要写代码）
 *
 * 用途：做完 Day5_Ex1 ~ Day5_Ex5 后，直接运行本文件，
 * 把今天学的所有知识点完整过一遍：
 *   类定义 → new 创建对象 → 给属性赋值 → 构造方法 → this → 对象方法调用
 *
 * 运行：VS Code 打开本文件 → 右上角 ▶ Run
 */
public class Day5_Review {
    public static void main(String[] args) {
        // 1. 创建对象 + 属性赋值 + 调用方法
        Student s = new Student("小明", 18);
        s.printInfo();
        Student s2 = new Student("小红", 19);
        s2.printInfo();

        // 2. 多个独立对象互不影响
        System.out.println("--- 对象之间互不影响 ---");
        s.age = 99;                 // 只改 s，不影响 s2
        System.out.println("s.age = " + s.age + "，s2.age = " + s2.age);

        // 3. 长方形：属性 + 方法
        Rectangle r = new Rectangle();
        r.width = 4;
        r.height = 6;
        System.out.println("--- 长方形 ---");
        System.out.println("面积：" + r.area() + "，周长：" + r.perimeter());

        // 4. 构造方法 + this
        Dog dog = new Dog("旺财", 3);
        dog.bark();

        // 5. 综合：银行账户
        System.out.println("--- 银行账户 ---");
        BankAccount acc = new BankAccount(1000);
        acc.deposit(500);
        acc.withdraw(200);
        acc.withdraw(99999);        // 余额不足
        System.out.println("余额：" + acc.getBalance());
    }
}

// ========== 下面的类对应 5 道练习题的答案，复习时对照着看 ==========

class Student {
    String name;
    int age;

    Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    void printInfo() {
        System.out.println("我叫" + name + "，今年" + age + "岁");
    }
}

class Rectangle {
    double width;
    double height;

    double area() {
        return width * height;
    }

    double perimeter() {
        return (width + height) * 2;
    }
}

class Dog {
    String name;
    int age;

    Dog(String name, int age) {
        this.name = name;
        this.age = age;
    }

    void bark() {
        System.out.println(name + "：汪汪！");
    }
}

class BankAccount {
    double balance;

    BankAccount(double balance) {
        this.balance = balance;
    }

    void deposit(double money) {
        balance += money;
    }

    void withdraw(double money) {
        if (balance < money) {
            System.out.println("余额不足");
        } else {
            balance -= money;
        }
    }

    double getBalance() {
        return balance;
    }
}