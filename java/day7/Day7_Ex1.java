public class Day7_Ex1 {
    public static void main(String[] args) {
        // 题 1：Animal 父类 + Dog 子类——继承入门
        Dog d = new Dog("旺财");
        d.eat(); // 期望：旺财在吃东西（父类方法）
        d.bark(); // 期望：旺财汪汪叫（子类自己的方法）
    }
}

// ===== 你的代码写在这里（Animal 类 + Dog 类 extends Animal）=====
class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    void eat() {
        System.out.println(name + "在吃东西");
    }
}

class Dog extends Animal {
    Dog(String name) {
        super(name);
    }

    void bark() {
        System.out.println(name + "汪汪叫");
    }
}
// ===========================================