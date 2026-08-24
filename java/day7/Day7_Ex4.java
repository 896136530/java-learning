public class Day7_Ex4 {
    public static void main(String[] args) {
        // 题 4：protected 属性 + super 调父类方法
        Student s = new Student("小红");
        System.out.println("名字：" + s.name);  // 期望：名字：小红（protected 子类能用）
        s.introduce();  // 期望：我是人，我叫小红 / 我还是学生
    }
}

// ===== 你的代码写在这里（Person 类 protected name + Student 类重写 introduce）=====


// ===========================================