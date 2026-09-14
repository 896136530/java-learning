import java.util.*;

public class Day23_Ex2 {
    public static void main(String[] args) {
        // 题 2：用 List 管一个班
        List<Student> students = new ArrayList<>();
        students.add(new Student("张三", 88));
        students.add(new Student("李四", 95));
        students.add(new Student("王五", 76));

        System.out.println("共 " + TeamUtil.count(students) + " 人");   // 期望：共 3 人
        TeamUtil.printAll(students);                                    // 期望：张三 88 / 李四 95 / 王五 76
        System.out.println("总分：" + TeamUtil.total(students));          // 期望：总分：259
    }
}

// ===== 你的代码写在这里：class Student（和 Ex1 一样）+ class TeamUtil =====

// TODO：① class Student：字段 name / score + 构造方法 + toString（返回 "姓名 分数"）
// TODO：② class TeamUtil：
//   static int count(List<Student> list)      → 返回人数
//   static void printAll(List<Student> list)  → 增强 for 挨个 System.out.println(s)
//   static int total(List<Student> list)      → 增强 for 累加 s.score

// ===========================================
