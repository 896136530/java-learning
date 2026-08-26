public class Day10_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——toString + equals + hashCode 三件套
        Book b1 = new Book("Java入门", 59);
        Book b2 = new Book("Java入门", 59);
        System.out.println(b1);                                       // 期望：书名：Java入门，价格：59
        System.out.println("内容相等？" + b1.equals(b2));              // 期望：true
        System.out.println("哈希相等？" + (b1.hashCode() == b2.hashCode())); // 期望：true
    }
}

// ===== 你的代码写在这里（类 Book：title/price + 重写三件套）=====

// ===========================================