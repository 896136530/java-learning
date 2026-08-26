public class Day10_Ex3 {
    public static void main(String[] args) {
        // 题 3：重写 equals——让对象按"内容"比
        Person p1 = new Person("小明", 18);
        Person p2 = new Person("小明", 18);
        Person p3 = new Person("小红", 20);
        System.out.println("p1==p2：" + (p1 == p2));           // 期望：false（地址不同）
        System.out.println("p1.equals(p2)：" + p1.equals(p2)); // 期望：true（重写后比内容）
        System.out.println("p1.equals(p3)：" + p1.equals(p3)); // 期望：false（内容不同）
    }
}

// ===== 你的代码写在这里（类 Person：name/age + 重写 equals(Object o)）=====

// ===========================================