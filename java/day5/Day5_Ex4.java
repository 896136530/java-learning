public class Day5_Ex4 {
    public static void main(String[] args) {
        // 题 4：构造方法参数名和属性名一样，用 this 区分
        Phone p = new Phone("小米", 2999);
        p.showInfo(); // 应输出：品牌：小米，价格：2999.0元
    }
}

// ===== 你的代码写在这里（定义 Phone 类：brand、price 属性 + 带 this 的构造方法 + showInfo）=====
class Phone {
    String name;
    int price;

    Phone(String name, int price) {
        this.name = name;
        this.price = price;
    }

    void showInfo() {
        System.out.println("品牌：" + name + "，价格：" + price + "元");
    }
}

// ===========================================