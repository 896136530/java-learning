public class Day16_Ex1 {
    public static void main(String[] args) {
        // 题 1：泛型盒——装什么类型，用的时候定
        Box<String> box = new Box<>();
        box.set("你好");
        System.out.println(box.get());   // 期望：你好

        Box<Integer> num = new Box<>();
        num.set(42);
        System.out.println(num.get());   // 期望：42
    }
}

// ===== 你的代码写在这里（类 Box<T>：set / get）=====

// ===========================================