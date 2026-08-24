public class Day7_Review {
    public static void main(String[] args) {
        // ======== 继承完整示例：一个动物世界 ========
        // 父类 Animal：name + eat
        // 子类 Dog / Cat：重写 sound()
        // 子类 CircusDog：super 调用父类方法 + 继承链条

        Dog d = new Dog("旺财");
        d.eat();        // 旺财在吃东西（父类方法）
        d.sound();      // 狗狗汪汪叫（重写）

        Cat c = new Cat("咪咪");
        c.eat();        // 咪咪在吃东西（父类方法）
        c.sound();      // 猫咪喵喵叫（重写）

        CircusDog cd = new CircusDog("明星");
        cd.eat();        // 明星在吃东西（两层上来的父类方法）
        cd.sound();      // 狗狗汪汪叫（继承 Dog 的重写）
        cd.perform();    // 明星还会表演！（子类新方法，还调了 super 的方法）
    }
}

// ---------- 父类 ----------
class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    void eat() {
        System.out.println(name + "在吃东西");
    }

    void sound() {
        System.out.println(name + "发出声音");
    }
}

// ---------- 子类 1 ----------
class Dog extends Animal {
    Dog(String name) {
        super(name);
    }

    @Override
    void sound() {
        System.out.println(name + "汪汪叫");
    }
}

// ---------- 子类 2 ----------
class Cat extends Animal {
    Cat(String name) {
        super(name);
    }

    @Override
    void sound() {
        System.out.println(name + "喵喵叫");
    }
}

// ---------- 孙类（多级继承）----------
class CircusDog extends Dog {
    CircusDog(String name) {
        super(name);
    }

    void perform() {
        super.sound();    // 调"最近的父类"的方法（Dog 的重写）
        System.out.println(name + "还会表演杂技！");
    }
}
// ===========================================