import java.util.HashMap;

public class Day13_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——投票统计 + 获胜者
        String[] votes = {"张三", "李四", "张三", "王五", "张三", "李四"};
        HashMap<String, Integer> result = VoteUtil.countVotes(votes);
        System.out.println("张三票数：" + result.get("张三"));     // 期望：张三票数：3
        System.out.println("获胜者：" + VoteUtil.winner(result));  // 期望：获胜者：张三
    }
}

// ===== 你的代码写在这里（类 VoteUtil：countVotes / winner 两个 static 方法）=====

// ===========================================