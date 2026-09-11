import java.util.*;

public class Day22_Ex5 {
    public static void main(String[] args) {
        // 题 5：Comparator 定制排序——姓名短的排前面，一样长按字典序
        Student[] arr = {
            new Student("王五", 99),
            new Student("张三丰", 88),
            new Student("李四", 77),
            new Student("欧阳修", 66)
        };
        Arrays.sort(arr, new NameLenComparator());
        for (Student s : arr) {
            System.out.println(s.name);
        }
        // 期望输出：
        //   李四
        //   王五
        //   张三丰
        //   欧阳修
    }
}

// ===== 你的代码写在这里：class Student + class NameLenComparator =====

// TODO：Student 类（name、score 字段 + 构造方法，不用 Comparable）
// TODO：NameLenComparator implements Comparator<Student>
//   compare(a, b) → 先比 name.length()（短的在前），长度相等再比 name 字典序（compareTo）

// ===========================================