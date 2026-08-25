public class Day8_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——动物喂食员：一个 feed 方法喂所有鸟
        feed(new Penguin("小企鹅")); // 期望：小企鹅在吃鱼 / 小企鹅不会飞
        feed(new Eagle("大鹏")); // 期望：大鹏在吃肉 / 大鹏飞得又高又快
    }

    // 参数用父类 Bird：多态参数，喂谁是谁
    static void feed(Bird b) {
        b.eat();
        b.fly();
    }
}

// ===== 你的代码写在这里（Bird 父类 + Penguin、Eagle 两个子类，后两个重写 eat/fly）=====
class Bird{
    String name;
    Bird(String name){
        this.name=name;
    }
    void eat(){
        System.out.println(name+"在吃");
    }
    void fly(){
        System.out.println(name+"不会飞");
    }
}
class Penguin extends Bird{
    Penguin(String name){
        super(name);
    }
    void eat(){
        System.out.println(name+"在吃鱼");
    }
    void fly(){
        System.out.println(name+"不会飞");
    }   
}
class Eagle extends Bird{
    Eagle(String name){
        super(name);
    }
    void eat(){
        System.out.println(name+"在吃肉");
    }
    void fly(){
        System.out.println(name+"飞得又高又快");
    } 
}
// ===========================================