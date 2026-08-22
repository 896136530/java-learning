public class Day6_Ex4 {
    public static void main(String[] args) {
        // 题 4：Book 只读类——创建后书名作者不能改（没有 setter）
        Book b = new Book("三体", "刘慈欣");
        System.out.println("书名：" + b.getTitle());   // 期望：书名：三体
        System.out.println("作者：" + b.getAuthor());  // 期望：作者：刘慈欣

        // 💡 思考：这里能写 b.setTitle("三体II") 吗？——不能，根本没这个 setter！
    }
}

// ===== 你的代码写在这里（Book 类：private 属性 + 构造方法 + 只有 getter 没有 setter）=====


// ===========================================