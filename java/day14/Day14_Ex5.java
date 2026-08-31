import java.util.Queue;
import java.util.LinkedList;

public class Day14_Ex5 {
    public static void main(String[] args) {
        // 题 5：任务队列耗时——poll 循环处理，处理完队列自动空
        Queue<Integer> jobs = new LinkedList<>();
        jobs.offer(5);
        jobs.offer(3);
        jobs.offer(7);
        System.out.println("总耗时：" + TaskQueue.totalWait(jobs));  // 期望：总耗时：15
        System.out.println("队列空？" + jobs.isEmpty());             // 期望：队列空？true
    }
}

// ===== 你的代码写在这里（类 TaskQueue：static int totalWait(Queue<Integer> jobs)）=====

// ===========================================