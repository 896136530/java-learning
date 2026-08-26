public class Day9_Ex1 {
    public static void main(String[] args) {
        // 题 1：抽象类入门——抽象方法 + 子类实现
        Dog d = new Dog("旺财");
        d.eat(); // 期望：旺财啃骨头（抽象方法，子类实现）
        d.sleep(); // 期望：旺财在睡觉（普通方法，父类现成的）
    }
}

// ===== 你的代码写在这里（抽象类 Animal + 子类 Dog，Dog 实现 eat）=====
abstract class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    abstract void eat(); // 抽象方法：只声明，子类实现

    void sleep() { // 普通方法：父类直接写好，子类不用管
        System.out.println(name + "在睡觉");
    }
}

class Dog extends Animal {
    Dog(String name) {
        super(name);
    }

    @Override
    void eat() {
        System.out.println(name + "啃骨头");
    }
}
// ===========================================