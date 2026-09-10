import java.net.*;
import java.io.*;

public class Day20_Ex3 {
    public static void main(String[] args) throws Exception {
        // 题 3：加法服务器——客户端连发两行数字，服务器回「a + b = 和」
        Thread sv = new Thread(() -> {
            SumServer.start(31103);
        });
        sv.setDaemon(true);   // 守护线程：main 结束 JVM 就退出,不挂死
        sv.start();
        Thread.sleep(300);

        Socket c = new Socket("127.0.0.1", 31103);
        PrintWriter out = new PrintWriter(c.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        out.println("3");
        out.println("4");
        System.out.println(in.readLine());   // 期望输出：3 + 4 = 7
        c.close();
    }
}

// ===== 你的代码写在这里（类 SumServer：static void start(int port)——accept 后读两行，Integer.parseInt 求和，回「a + b = c」）=====
class SumServer{
    public static void start(int port){
        try{
            ServerSocket ss=new ServerSocket(port);
            while(true){
                Socket s=ss.accept();
                BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out=new PrintWriter(s.getOutputStream(),true);
                int a=Integer.parseInt(in.readLine());
                int b=Integer.parseInt(in.readLine());
                out.println(a+" + "+b+" = "+(a+b));
                s.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
// ===========================================