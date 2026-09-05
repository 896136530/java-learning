public class Day19_Ex1 {
    public static void main(String[] args) throws InterruptedException {
        // 题 1：分段求和——4 个工人线程各算一段，合并
        int n = 100, m = 4;
        Thread[] ts = new Thread[m];
        for (int i = 0; i < m; i++) {
            SumWorker w = new SumWorker(i * n / m + 1, (i + 1) * n / m);
            ts[i] = new Thread(w);
            ts[i].start();
        }
        for (Thread t : ts) {
            t.join();
        }
        System.out.println("总和：" + SumWorker.total);   // 期望：总和：5050
    }
}

// ===== 你的代码写在这里（类 SumWorker implements Runnable：start/end 构造；static int total；static synchronized void merge；run() 求和后 merge）=====

// ===========================================