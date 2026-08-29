public class Day11_Ex5 {
    public static void main(String[] args) {
        // 题 5：反转字符串——综合运用 charAt + 倒序遍历
        System.out.println(ReverseUtil.reverse("abc"));   // 期望：cba
        System.out.println(ReverseUtil.reverse("Hello")); // 期望：olleH
        System.out.println(ReverseUtil.reverse(""));      // 期望：（空串，不崩）
    }
}

// ===== 你的代码写在这里（类 ReverseUtil：static String reverse(String s)）=====
class ReverseUtil{
    public static String reverse(String x){
        String result="";        // ⚠️ 原来是 " "（带空格的），反转会多个前导空格、空串变空格
        for(int i=x.length()-1;i>=0;i--){
            result=result+x.charAt(i);
        }
        return result;
    }
}
// ===========================================