import java.net.*;
import java.io.*;

public class Day20_Ex2 {
    public static void main(String[] args) throws Exception {
        // 题 2：大写服务器——客户端发小写，服务器转大写回
        Thread sv = new Thread(() -> {
            UpperServer.start(31102);
        });
        sv.setDaemon(true);   // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300);

        Socket c = new Socket("127.0.0.1", 31102);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("hello");
        System.out.println(in.readLine());   // 期望输出：HELLO
        c.close();
    }
}

// ===== 你的代码写在这里（类 UpperServer：static void start(int port)——accept 后读一行，toUpperCase() 后回）=====

// ===========================================