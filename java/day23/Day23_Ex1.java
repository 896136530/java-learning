import java.util.*;

public class Day23_Ex1 {
    public static void main(String[] args) {
        // 题 1：造一个 Student 类——把姓名和成绩打包成一个对象
        Student s1 = new Student("张三", 88);
        Student s2 = new Student("李四", 95);
        System.out.println(s1);                                  // 期望输出：张三 88
        System.out.println(s2);                                  // 期望输出：李四 95
        System.out.println("最高分：" + Student.maxOf(s1, s2));   // 期望输出：最高分：95
    }
}

// ===== 你的代码写在这里：class Student =====

// TODO：Student 类
//   字段：String name / int score
//   构造方法：Student(String name, int score)
//   toString()：返回 "姓名 分数"（例如 张三 88），记得加 @Override
//   static int maxOf(Student a, Student b)：返回两人中较高的分数

// ===========================================
