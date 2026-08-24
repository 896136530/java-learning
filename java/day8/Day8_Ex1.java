public class Day8_Ex1 {
    public static void main(String[] args) {
        // 题 1：向上转型 + 动态绑定（Animal + Dog）
        Animal a = new Dog("旺财"); // 向上转型：父类引用指向子类对象
        a.sound(); // 期望：旺财汪汪叫（运行看右边！）
    }
}

// ===== 你的代码写在这里（Animal 父类 + Dog 子类，Dog 重写 sound）=====

// ===========================================