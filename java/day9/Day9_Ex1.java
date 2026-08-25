public class Day9_Ex1 {
    public static void main(String[] args) {
        // 题 1：抽象类入门——抽象方法 + 子类实现
        Dog d = new Dog("旺财");
        d.eat();   // 期望：旺财啃骨头（抽象方法，子类实现）
        d.sleep(); // 期望：旺财在睡觉（普通方法，父类现成的）
    }
}

// ===== 你的代码写在这里（抽象类 Animal + 子类 Dog，Dog 实现 eat）=====

// ===========================================