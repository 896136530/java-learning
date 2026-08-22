public class Day6_Ex1 {
    public static void main(String[] args) {
        // 题 1：Student 封装——setter 赋值，getter 读取
        Student s = new Student();
        s.setName("小明");
        s.setAge(18);

        System.out.println("名字：" + s.getName());  // 期望：名字：小明
        System.out.println("年龄：" + s.getAge());   // 期望：年龄：18

        s.printInfo();  // 期望：我叫小明，今年18岁
    }
}

// ===== 你的代码写在这里（Student 类：private 属性 + getter/setter + printInfo）=====


// ===========================================