import java.util.Queue;
import java.util.LinkedList;

public class Day14_Ex2 {
    public static void main(String[] args) {
        // 题 2：叫号排队——先进先出
        Queue<String> queue = new LinkedList<>();
        queue.offer("张三");
        queue.offer("李四");
        queue.offer("王五");
        System.out.println("叫号：" + QueueSim.serveNext(queue));   // 期望：叫号：张三
        System.out.println("下一个：" + queue.peek());              // 期望：下一个：李四
        System.out.println("还剩：" + queue.size());                // 期望：还剩：2
    }
}

// ===== 你的代码写在这里（类 QueueSim：static String serveNext(Queue<String> queue)）=====
class QueueSim{
    public static String serveNext(Queue<String>queue){
        String x=queue.poll();
    
    return x;
    }
}
// ===========================================