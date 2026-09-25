package ex5;

/** 图书实体（属性 + 构造器 + toString）—— 和 Day27 的 Student 一个套路 */
public class Book {

    public int id;
    public String title;      // 书名
    public String author;     // 作者
    public double price;      // 单价
    public int stock;         // 库存

    public Book(String title, String author, double price, int stock) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    public Book(int id, String title, String author, double price, int stock) {
        this(title, author, price, stock);
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title + " / " + author + " / " + price + " 元 / 库存 " + stock;
    }
}