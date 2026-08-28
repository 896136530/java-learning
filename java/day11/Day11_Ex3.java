public class Day11_Ex3 {
    public static void main(String[] args) {
        // 题 3：常用方法——大写/小写/去空格/首字母大写
        System.out.println(TextUtil.shout("hello"));      // 期望：HELLO
        System.out.println(TextUtil.whisper("WORLD"));    // 期望：world
        System.out.println(TextUtil.trimIt("   Java   ")); // 期望：Java
        System.out.println(TextUtil.upFirst("java"));     // 期望：Java（首字母大写）
    }
}

// ===== 你的代码写在这里（类 TextUtil：shout/whisper/trimIt/upFirst 四个 static 方法）=====

// ===========================================