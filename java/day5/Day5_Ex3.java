public class Day5_Ex3 {
    public static void main(String[] args) {
        // 题 3：带构造方法的 Dog 类
        Dog dog = new Dog("旺财", 3);
        dog.bark(); // 应输出：旺财：汪汪！
    }
}

// ===== 你的代码写在这里（定义 Dog 类：name、age 属性 + 构造方法 + bark 方法）=====
class Dog {
    String name;
    int age;

    Dog(String n, int m) {
        name = n;
        age = m;
    }

    void bark() {
        System.out.println(name + ":汪汪！");
    }
}
// ===========================================