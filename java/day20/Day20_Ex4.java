import java.net.*;
import java.io.*;

public class Day20_Ex4 {
    public static void main(String[] args) throws Exception {
        // 题 4：计数器服务器——同一个连接里每收到一行，回「第N条：」+原文（N 从 1 递增）
        Thread sv = new Thread(() -> {
            CounterServer.start(31104);
        });
        sv.setDaemon(true);   // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300);

        Socket c = new Socket("127.0.0.1", 31104);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("ping");
        System.out.println(in.readLine());   // 期望输出：第1条：ping
        out.println("pong");
        System.out.println(in.readLine());   // 期望输出：第2条：pong
        out.println("hi");
        System.out.println(in.readLine());   // 期望输出：第3条：hi
        c.close();
    }
}

// ===== 你的代码写在这里（类 CounterServer：static void start(int port)——accept 后循环读行，计数，回「第N条：」+原文，readLine 为 null 结束）=====

// ===========================================