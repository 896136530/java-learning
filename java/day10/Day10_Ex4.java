public class Day10_Ex4 {
    public static void main(String[] args) {
        // 题 4：getClass()——运行时拿真实类型（多态"看右边"的实锤）
        Animal a = new Dog("旺财");
        System.out.println("真实类型：" + a.getClass().getName()); // 期望：Dog
        System.out.println("是 Dog？" + (a instanceof Dog));      // 期望：true
        System.out.println("是 Animal？" + (a instanceof Animal)); // 期望：true
    }
}

// ===== 你的代码写在这里（父类 Animal + 子类 Dog，带 name 构造）=====

// ===========================================