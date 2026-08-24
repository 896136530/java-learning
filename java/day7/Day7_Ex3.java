public class Day7_Ex3 {
    public static void main(String[] args) {
        // 题 3：方法重写 @Override——同一个方法，不同子类不同表现
        Dog d = new Dog();
        Cat c = new Cat();
        d.sound();  // 期望：狗狗汪汪叫（重写后）
        c.sound();  // 期望：猫咪喵喵叫（重写后）
    }
}

// ===== 你的代码写在这里（Animal 类 + Dog 类 + Cat 类，后两个重写 sound）=====


// ===========================================