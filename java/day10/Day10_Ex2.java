public class Day10_Ex2 {
    public static void main(String[] args) {
        // 题 2：== vs equals——String 的坑 + 没重写 equals 的对象
        String a = "hello";
        String b = "hello";
        String c = new String("hello");
        Phone p1 = new Phone("小米");
        Phone p2 = new Phone("小米");
        System.out.println("a==b：" + (a == b));           // 期望：true（字面量共用常量池）
        System.out.println("a==c：" + (a == c));           // 期望：false（new 出新对象，地址不同）
        System.out.println("a.equals(c)：" + a.equals(c)); // 期望：true（String 重写 equals，比内容）
        System.out.println("p1==p2：" + (p1 == p2));       // 期望：false（两个对象，地址不同）
        System.out.println("p1.equals(p2)：" + p1.equals(p2)); // 期望：false（没重写 equals = 默认比地址）
    }
}

// ===== 你的代码写在这里（类 Phone：brand 属性 + 构造，不重写 equals）=====

// ===========================================