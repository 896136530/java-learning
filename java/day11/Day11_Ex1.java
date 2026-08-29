public class Day11_Ex1 {
    public static void main(String[] args) {
        // 题 1：String 比较——内容相等永远用 equals，别用 ==
        System.out.println(CompareUtil.isSame("hello", "hello"));          // 期望：true
        System.out.println(CompareUtil.isSame(new String("hello"), "hello")); // 期望：true（内容相等）
        System.out.println(CompareUtil.isSame("hello", "HELLO"));          // 期望：false（大小写敏感）
    }
}

// ===== 你的代码写在这里（类 CompareUtil：static boolean isSame(String a, String b)）=====
class CompareUtil{

    public static boolean isSame(String a,String b ){
        return a.equals(b);
    }
}
// ===========================================