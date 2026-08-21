public class Day4_Ex4 {
    public static void main(String[] args) {
        // 题 4：用方法重载计算面积（三个同名 area 方法）
        System.out.println("正方形面积：" + area(5));
        System.out.println("长方形面积：" + area(4, 6));
        System.out.println("圆的面积：" + area(3.0));
    }

    // ===== 你的代码写在这里（定义三个重载的 area 方法：正方形/长方形/圆）=====
public static  int area(int a){
    return a*a;
}
public static  int area(int a,int b){
    return a*b;
}
public static  double area(double r){
    return 3.14*r*r;
}   

    // ===========================================
}