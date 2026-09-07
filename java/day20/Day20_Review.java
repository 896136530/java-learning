import java.net.*;
import java.io.*;

public class Day20_Review {
    public static void main(String[] args) throws Exception {
        // ======== 网络全家桶：后台服务器线程 + 客户端发两行 + 逐行收 [echo] 前缀回显 ========
        Thread sv = new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(31199);
                while (true) {
                    Socket s = ss.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    String line;
                    while ((line = in.readLine()) != null) {
                        out.println("[echo] " + line);
                        if (line.equals("bye")) {
                            s.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        sv.setDaemon(true);   // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300);

        Socket c = new Socket("127.0.0.1", 31199);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("hello");
        System.out.println(in.readLine());   // [echo] hello
        out.println("bye");
        System.out.println(in.readLine());   // [echo] bye
        c.close();
    }
}
// ===========================================