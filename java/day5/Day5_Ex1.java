public class Day5_Ex1 {
    public static void main(String[] args) {
        // 题 1：创建两个学生对象，分别调用 printInfo()
        Student s1 = new Student();
        s1.name = "小明";
        s1.age = 18;
        s1.printInfo(); // 应输出：我叫小明，今年18岁

        Student s2 = new Student();
        s2.name = "小红";
        s2.age = 19;
        s2.printInfo();
    }
}

// ===== 你的代码写在这里（定义 Student 类：属性 name、age + 方法 printInfo）=====
class Student {
    String name;
    int age;

    void printInfo() {
        System.out.println("我叫" + name + ",今年" + age + "岁");
    }
}

// ===========================================