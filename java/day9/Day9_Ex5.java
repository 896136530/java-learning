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
abstract class Pet{
    String name;
    Pet(String name){
        this.name=name;
    }
    abstract void eat();   // 抽象方法：每种宠物吃什么不同，子类实现
    void sleep(){          // 普通方法：都在睡觉，父类写好
        System.out.println(name+"在睡觉");
    }
}
interface Soundable{
    void makeSound();
}
class Cat extends Pet implements Soundable{
    Cat(String name){
        super(name);
    }
    @Override
    void eat(){
        System.out.println(name+"吃鱼");
    }
    public void makeSound(){
        System.out.println(name+"喵喵叫");
    }
}
class Duck extends Pet implements Soundable{
    Duck(String name){
        super(name);
    }
    @Override
    void eat(){
        System.out.println(name+"吃谷物");
    }
    public void makeSound(){
        System.out.println(name+"嘎嘎叫");
    }
}
// ===========================================