public class Day8_Ex3 {
    public static void main(String[] args) {
        // 题 3：多态数组——一个数组装不同类型，遍历时各自表现
        Shape[] shapes = { new Circle(5), new Rect(4, 6) }; // 圆：半径 5；矩形：4x6
        for (int i = 0; i < shapes.length; i++) {
            System.out.println("面积：" + shapes[i].area()); // 期望：78.5 / 24.0
        }
    }
}

// ===== 你的代码写在这里（Shape 父类 + Circle、Rect 两个子类，后两个重写 area）=====

// ===========================================