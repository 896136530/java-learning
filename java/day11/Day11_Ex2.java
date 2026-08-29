public class Day11_Ex2 {
    public static void main(String[] args) {
        // 题 2：length 和 charAt——字符串的下标世界
        System.out.println(CharUtil.len("Java"));       // 期望：4（字符个数）
        System.out.println(CharUtil.firstChar("Java")); // 期望：J（下标 0）
        System.out.println(CharUtil.lastChar("Java"));  // 期望：a（下标 length-1）
    }
}

// ===== 你的代码写在这里（类 CharUtil：len/firstChar/lastChar 三个 static 方法）=====
class CharUtil{
    public static int len(String a){
        return a.length();
    }
    public static char firstChar(String b){
        return b.charAt(0);
    }
    public static char lastChar(String c){
        return c.charAt(c.length()-1);
    }
}
// ===========================================