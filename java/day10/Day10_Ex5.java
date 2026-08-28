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
class Book{
    String title;
    int price;
    Book(String title,int price){
        this.title=title;
        this.price=price;
    }
    @Override
    public String toString(){
        return "书名："+title+"，价格："+price;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Book)) return false;   // ⚠️ 少了这行，传进来非 Book 就崩
        Book p=(Book)o;
        return title.equals(p.title)&&price==(p.price);
    }
    @Override
    public int hashCode(){
        return title.hashCode()+price;
    }
}
// ===========================================