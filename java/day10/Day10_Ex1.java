public class Day10_Ex1 {
    public static void main(String[] args) {
        // 题 1：重写 toString——让 println(对象) 打出有用信息
        Student s = new Student("小明", 2025001);
        System.out.println(s);            // 期望：姓名：小明，学号：2025001
        System.out.println(s.toString()); // 期望：姓名：小明，学号：2025001
    }
}

// ===== 你的代码写在这里（类 Student：name/studentId + 重写 toString）=====

// ===========================================