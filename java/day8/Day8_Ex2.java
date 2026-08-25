public class Day8_Ex2 {
    public static void main(String[] args) {
        // 题 2：多态参数——一个方法（参数用父类）通吃所有子类
        show(new Apple("红富士")); // 期望：我是苹果：红富士
        show(new Orange("甜橙")); // 期望：我是橘子：甜橙
    }

    // 参数用父类 Fruit：装谁就是谁（多态参数）
    static void show(Fruit f) {
        f.info();
    }
}

// ===== 你的代码写在这里（Fruit 父类 + Apple、Orange 两个子类，后两个重写 info）=====
class Fruit{
    String name;
    Fruit(String name){
        this.name=name;
    }
    void info(){
        System.out.println("我是水果："+name);
    }
}
class Apple extends Fruit{
    Apple(String name){
        super(name);
    }
    @Override
    void info(){
        System.out.println("我是苹果："+name);
    }
}
class Orange extends Fruit{
    Orange(String name){
        super(name);
    }
    @Override
    void info(){
        System.out.println("我是橘子："+name);
    }
}
// ===========================================