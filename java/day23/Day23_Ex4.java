import java.util.*;

public class Day23_Ex4 {
    public static void main(String[] args) {
        // 题 4：成绩榜——分数降序，同分按姓名升序
        List<Student> list = new ArrayList<>();
        list.add(new Student("张三", 88));
        list.add(new Student("李四", 95));
        list.add(new Student("王五", 88));
        list.add(new Student("赵六", 76));

        Collections.sort(list);            // 靠 Student 自己的 compareTo 排
        for (Student s : list) {
            System.out.println(s);
        }
        // 期望输出：
        //   李四 95
        //   张三 88
        //   王五 88
        //   赵六 76
    }
}

// ===== 你的代码写在这里：class Student（实现 Comparable<Student>）=====

// TODO：Student 类
//   字段：String name / int score + 构造方法
//   实现 Comparable<Student>，重写 compareTo(Student o)：
//     ① 先比分数，分数高的排前面 → int c = Integer.compare(o.score, this.score);
//     ② c != 0 就直接 return c
//     ③ 分数相同时，返回 this.name.compareTo(o.name)（姓名升序兜底，保证顺序稳定！）
//   toString()：返回 "姓名 分数"

// ===========================================
