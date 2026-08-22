public class Day5_Ex2 {
    public static void main(String[] args) {
        // 题 2：长方形面积和周长
        Rectangle r = new Rectangle();
        r.width = 4;
        r.height = 6;
        System.out.println("面积：" + r.area());        // 期望 24.0
        System.out.println("周长：" + r.perimeter());    // 期望 20.0
    }
}

// ===== 你的代码写在这里（定义 Rectangle 类：width、height 属性 + area、perimeter 方法）=====
class Rectangle{
    double width;
    double height;
    double area(){
        return width*height;
    }
    double perimeter(){
return (width*2+height*2);
}
}

// ===========================================