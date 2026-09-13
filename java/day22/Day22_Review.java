import java.util.*;

public class Day22_Review {
    public static void main(String[] args) {
        // Review：学号→学生 存进 TreeMap，按成绩从高到低排出前三名
        TreeMap<Integer, Student> students = new TreeMap<>();
        students.put(101, new Student("张三", 88));
        students.put(102, new Student("李四", 95));
        students.put(103, new Student("王五", 76));
        students.put(104, new Student("赵六", 60));
        students.put(105, new Student("钱七", 100));
        RankBoard.printTop3(students);
        // 期望输出：
        //   第1名：钱七 100
        //   第2名：李四 95
        //   第3名：张三 88
    }
}

// ===== 你的代码写在这里：class Student + class RankBoard =====

class Student {
    String name;
    int score;
    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }
}

class RankBoard {
    static void printTop3(TreeMap<Integer, Student> students) {
        List<Map.Entry<Integer, Student>> list = new ArrayList<>(students.entrySet());
        list.sort((a, b) -> b.getValue().score - a.getValue().score);   // 按分数降序
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            Map.Entry<Integer, Student> e = list.get(i);
            System.out.println("第" + (i + 1) + "名：" + e.getValue().name + " " + e.getValue().score);
        }
    }
}
// ===========================================