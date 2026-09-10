import java.net.*;
import java.io.*;

public class Day20_Ex1 {
    public static void main(String[] args) throws Exception {
        // 题 1：回声服务器——客户端发什么，服务器回什么
        Thread sv = new Thread(() -> {
            EchoServer.start(31101);
        });
        sv.setDaemon(true); // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300); // 等服务器 accept 起来

        Socket c = new Socket("127.0.0.1", 31101);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("你好，服务器");
        System.out.println(in.readLine()); // 期望输出：服务器回话：你好，服务器
        c.close();
    }
}

// ===== 类 EchoServer：static void start(int port)——accept
// 循环你的代码写在这里（，读到一行回「服务器回话：」+原文）=====
class EchoServer {

    public static void start(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket s = ss.accept();
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    out.println("服务器回话：" + line);
                }
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
// ===========================================