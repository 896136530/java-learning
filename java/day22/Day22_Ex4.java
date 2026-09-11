import java.util.*;

public class Day22_Ex4 {
    public static void main(String[] args) {
        // 题 4：Student 实现 Comparable，按分数降序排
        TreeSet<Student> set = new TreeSet<>();
        set.add(new Student("张三", 88));
        set.add(new Student("李四", 95));
        set.add(new Student("王五", 76));
        for (Student s : set) {
            System.out.println(s.name + " " + s.score);
        }
        // 期望输出：
        //   李四 95
        //   张三 88
        //   王五 76
    }
}

// ===== 你的代码写在这里：class Student（实现 Comparable<Student>） =====

// TODO：Student 类
//   字段：String name / int score，构造方法 (name, score)
//   实现 Comparable<Student>，重写 compareTo → 分数高的排前面（倒过来比！）

// ===========================================