public class Day11_Review {
    public static void main(String[] args) {
        // ======== String 全家桶完整示例：短信工具 ========
        String phone = "  138-1234-5678  ";
        phone = phone.trim();                                  // 去首尾空格
        System.out.println("清理后：" + phone);                // 清理后：138-1234-5678
        System.out.println("号码长度：" + phone.length());     // 号码长度：13
        System.out.println("显示前3位：" + phone.substring(0, 3)); // 显示前3位：138
        String pwd = "abc123";
        System.out.println("密码大写：" + pwd.toUpperCase());  // 密码大写：ABC123
        System.out.println("是否以 abc 开头：" + pwd.startsWith("abc")); // true
        System.out.println("是否含 123：" + pwd.contains("123"));        // true
        System.out.println("倒过来：" + reverse(pwd));          // 倒过来：321cba
        String s = "abc";
        s.toUpperCase();                                       // 没接住返回值！
        System.out.println("原字符串没变：" + s);               // abc（不可变）
    }

    static String reverse(String s) {
        String r = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            r = r + s.charAt(i);
        }
        return r;
    }
}
// ===========================================