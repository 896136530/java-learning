public class Day9_Ex3 {
    public static void main(String[] args) {
        // 题 3：一个类实现多个接口（弥补单继承）
        Athlete a = new Athlete("小明");
        a.swim(); // 期望：小明在蛙泳（接口1）
        a.run();  // 期望：小明在冲刺跑（接口2）
        Swimmer s = a; // 接口引用也能指向实现类
        s.swim(); // 期望：小明在蛙泳
    }
}

// ===== 你的代码写在这里（接口 Swimmer、接口 Runner + 类 Athlete 实现两个接口）=====
interface Swimmer{
    void swim();
}
interface Runner{
    void run();   // ⚠️ 原来写成了 Run()，大写 R 和小写 r 是两个方法名，实现不了
}
class Athlete implements Swimmer,Runner{
    String name;
    Athlete(String name){
        this.name=name;
    }
    @Override
    public void swim(){
        System.out.println(name+"在蛙泳");
    }
    @Override
    public void run(){
        System.out.println(name+"在冲刺跑");
    }
}
// ===========================================