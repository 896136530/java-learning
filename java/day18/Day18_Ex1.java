public class Day18_Ex1 {
    public static void main(String[] args) throws InterruptedException {
        // 题 1：继承 Thread 的问候线程
        HelloThread t = new HelloThread();
        t.start();
        t.join();                          // 等 t 跑完
        System.out.println("主线程结束");
    }
}

// ===== 你的代码写在这里（类 HelloThread extends Thread：run() 打印 3 次「线程1：第i次」）=====
class HelloThread extends Thread {
    @Override
    public void run(){
        for(int i=0;i<3;i++){
            System.out.println("线程1：第"+i+"次");
        }
    }
}
// ===========================================