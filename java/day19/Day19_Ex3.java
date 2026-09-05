import java.util.*;

public class Day19_Ex3 {
    public static void main(String[] args) throws InterruptedException {
        // 题 3：线程安全列表——两个线程各塞 5000 个元素
        Thread t1 = new Thread(new ListAdder());
        Thread t2 = new Thread(new ListAdder());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("列表大小：" + ListAdder.list.size());   // 期望：列表大小：10000
    }
}

// ===== 你的代码写在这里（类 ListAdder implements Runnable：static List<String> list = Collections.synchronizedList(new ArrayList<>())；run() 循环 5000 次 add("x")）=====

// ===========================================