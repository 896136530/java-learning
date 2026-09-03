public class Day16_Ex4 {
    public static void main(String[] args) {
        // 题 4：泛型方法 printAll——空格隔开打印一行
        String[] pets = {"猫", "狗", "鸟"};
        Printer.printAll(pets);   // 期望：猫 狗 鸟
    }
}

// ===== 你的代码写在这里（类 Printer：static <T> void printAll(T[] arr)）=====
class Printer{
    public static <T> void printAll(T[] arr){
        for(T s:arr){
            System.out.print(s+" ");
        }
        System.out.println();
    }
}
// ===========================================