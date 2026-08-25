public class Day9_Ex4 {
    public static void main(String[] args) {
        // 题 4：抽象类 + 接口组合——"是什么"用抽象类，"能干什么"用接口
        JavaDev d = new JavaDev("小李");
        d.work();      // 期望：小李开发系统（抽象方法实现）
        d.writeCode(); // 期望：小李写 Java 代码（接口方法）
        d.punchCard(); // 期望：小李打卡上班（父类普通方法）
    }
}

// ===== 你的代码写在这里（抽象类 Employee + 接口 Coder + 类 JavaDev 组合）=====

// ===========================================