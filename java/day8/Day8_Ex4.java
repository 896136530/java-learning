public class Day8_Ex4 {
    public static void main(String[] args) {
        // 题 4：instanceof + 向下转型——想用子类独有的方法，先判断再转
        Person p = new Student("小明"); // 向上转型
        System.out.println("p 是学生？" + (p instanceof Student)); // 期望：p 是学生？true
        p.introduce(); // 期望：我是人，我叫小明（父类方法，直接能调）
        // study() 是 Student 独有的，必须向下转型才能调
        if (p instanceof Student) { // 先判断，安全转型
            Student s = (Student) p; // 向下转型
            s.study(); // 期望：小明在学习
        }
    }
}

// ===== 你的代码写在这里（Person 父类 + Student 子类，Student 加新方法 study）=====
class Person{
    String name;
    Person(String name){
        this.name=name;
    }
    void introduce(){
        System.out.println("我是人，我叫"+name);
    }
}
class Student extends Person{
    Student(String name){
        super(name);
    }
    void study(){
        System.out.println(name+"在学习");
    }
}
// ===========================================