public class Day9_Ex2 {
    public static void main(String[] args) {
        // 题 2：接口入门——接口引用指向实现类（接口多态）
        Flyable f1 = new Bird("麻雀");
        f1.fly(); // 期望：麻雀扇翅膀飞了
        Flyable f2 = new Plane("波音747");
        f2.fly(); // 期望：波音747喷气起飞
    }
}

// ===== 你的代码写在这里（接口 Flyable + 类 Bird、类 Plane，后两个实现 fly）=====

// ===========================================