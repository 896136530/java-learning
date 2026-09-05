public class Day18_Ex2 {
    public static void main(String[] args) throws InterruptedException {
        // 题 2：同一个 Runnable 任务，起两个线程
        Runner r = new Runner();
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("完成");
    }
}

// ===== 你的代码写在这里（类 Runner implements Runnable：run() 打印一次「跑起来了」）=====
class Runner implements Runnable{
    @Override 
    public void run(){
        System.out.println("跑起来了");
    }
}
// ===========================================