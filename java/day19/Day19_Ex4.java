public class Day19_Ex4 {
    public static void main(String[] args) throws InterruptedException {
        // 题 4：下载进度条——下载线程跑满 100，主线程 join 等待
        Thread d = new Thread(new Downloader());
        d.start();
        d.join();
        System.out.println("进度：" + Downloader.progress + " 下载完成");   // 期望：进度：100 下载完成
    }
}

// ===== 你的代码写在这里（类 Downloader implements Runnable：static int progress=0；run() 循环 100 次：sleep(10) 后 progress++）=====

// ===========================================