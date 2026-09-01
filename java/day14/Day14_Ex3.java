import java.util.LinkedList;

public class Day14_Ex3 {
    public static void main(String[] args) {
        // 题 3：头尾操作——LinkedList 两头快
        LinkedList<String> list = new LinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println("头：" + LinkedOps.first(list)); // 期望：头：a
        LinkedOps.pushFront(list, "x");
        System.out.println("头：" + LinkedOps.first(list)); // 期望：头：x
        System.out.println("尾：" + LinkedOps.last(list)); // 期望：尾：c
        System.out.println("长度：" + list.size()); // 期望：长度：4
    }
}

// ===== 你的代码写在这里（类 LinkedOps：first / last / pushFront 三个 static 方法）=====
class LinkedOps {
    public static String first(LinkedList<String> list) {
        String x = list.getFirst();
        return x;
    }
    public static String last(LinkedList<String> list){
        String x=list.getLast();
        return x;
}
    public static void pushFront(LinkedList<String> list,String x){
        list.addFirst(x);
    }

}
// ===========================================