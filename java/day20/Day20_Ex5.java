import java.net.*;
import java.io.*;

public class Day20_Ex5 {
    public static void main(String[] args) throws Exception {
        // 题 5：多人服务器——每来一个客户端开一个线程，线程里读名字回「欢迎，XX！」
        Thread sv = new Thread(() -> {
            MultiServer.start(31105);
        });
        sv.setDaemon(true);   // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300);

        Socket c = new Socket("127.0.0.1", 31105);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("小明");
        System.out.println(in.readLine());   // 期望输出：欢迎，小明！
        c.close();
    }
}

// ===== 你的代码写在这里（类 MultiServer：static void start(int port)——循环 accept，每个连接 new Thread 开线程：读一行名字，回「欢迎，」+名字+「！」）=====

// ===========================================