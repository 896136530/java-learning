import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Day14_Review {
    public static void main(String[] args) {
        // ======== 三张新牌全家桶：员工中心 ========
        HashSet<String> ids = new HashSet<>();
        System.out.println("工号 1001 加入：" + ids.add("1001"));  // true
        System.out.println("工号 1001 再报：" + ids.add("1001"));  // false（重复被拒）
        ids.add("1002");
        System.out.println("在职工号数：" + ids.size());          // 2
        System.out.println("有 1002？" + ids.contains("1002"));   // true

        System.out.println("---- 工号遍历（顺序不定，正常）----");
        for (String id : ids) {
            System.out.println(id);
        }

        LinkedList<String> todos = new LinkedList<>();
        todos.addLast("写报告");
        todos.addFirst("开会");
        System.out.println("待办第一件：" + todos.getFirst());    // 开会
        System.out.println("待办最后一件：" + todos.getLast());   // 写报告
        todos.removeFirst();
        System.out.println("处理完第一件后：" + todos.getFirst()); // 写报告

        System.out.println("---- 办事窗口排队叫号 ----");
        Queue<String> line = new LinkedList<>();
        line.offer("客户A");
        line.offer("客户B");
        line.offer("客户C");
        while (!line.isEmpty()) {
            System.out.println("请 " + line.poll() + " 到 3 号窗口");
        }
        System.out.println("队伍空？" + line.isEmpty());            // true
    }
}
// ===========================================