public class Day18_Ex3 {
    public static void main(String[] args) throws InterruptedException {
        // 题 3：同步计数器——两个线程各加 500，结果必须 1000
        Thread t1 = new Thread(new Adder());
        Thread t2 = new Thread(new Adder());
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("计数结果：" + Adder.value);   // 期望：计数结果：1000
    }
}

// ===== 你的代码写在这里（类 Adder implements Runnable：static int value=0；static synchronized void add()；run() 循环 500 次 add()）=====

// ===========================================