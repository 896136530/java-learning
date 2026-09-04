public class Day18_Review {
    static int count = 0;

    static synchronized void add() {
        count++;
    }

    public static void main(String[] args) throws InterruptedException {
        // ======== 线程全家桶：Runnable 双线程 + synchronized + join ========
        Thread a = new Thread(new Runnable() {
            public void run() {
                System.out.println("线程A开始");
                for (int i = 0; i < 500; i++) {
                    add();
                }
            }
        });
        Thread b = new Thread(new Runnable() {
            public void run() {
                System.out.println("线程B开始");
                for (int i = 0; i < 500; i++) {
                    add();
                }
            }
        });
        a.start();
        a.join();      // 先跑完 A，保证"线程A开始"在"线程B开始"前面（顺序稳定）
        b.start();
        b.join();
        System.out.println("计数结果：" + count);
        System.out.println("主线程结束");
    }
}
// ===========================================