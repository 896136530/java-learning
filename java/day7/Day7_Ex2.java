public class Day7_Ex2 {
    public static void main(String[] args) {
        // 题 2：Person 父类 + Student 子类——super 调父类构造方法
        Student s = new Student("小明", 18, 2025001);
        s.showAll();  // 期望：姓名：小明，年龄：18，学号：2025001
    }
}

// ===== 你的代码写在这里（Person 类 + Student 类 with super(name, age)）=====


// ===========================================