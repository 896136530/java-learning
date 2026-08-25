public class Day9_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——宠物店：抽象类管"是宠物"，接口管"会叫"
        show(new Cat("猫咪"));
        speak(new Cat("猫咪"));
        show(new Duck("鸭鸭"));
        speak(new Duck("鸭鸭"));
    }

    // 参数用抽象类：通吃所有宠物（eat + sleep）
    static void show(Pet p) {
        p.eat();
        p.sleep();
    }

    // 参数用接口：通吃所有"会叫的"
    static void speak(Soundable s) {
        s.makeSound();
    }
}

// ===== 你的代码写在这里（接口 Soundable + 抽象类 Pet + 类 Cat、类 Duck）=====

// ===========================================