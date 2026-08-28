public class Day11_Ex4 {
    public static void main(String[] args) {
        // 题 4：字符统计——数一数某个字符出现几次
        System.out.println(SearchUtil.countChar("banana", 'a')); // 期望：3
        System.out.println(SearchUtil.countChar("hello", 'l'));  // 期望：2
        System.out.println(SearchUtil.countChar("java", 'x'));   // 期望：0
    }
}

// ===== 你的代码写在这里（类 SearchUtil：static int countChar(String s, char c)）=====

// ===========================================