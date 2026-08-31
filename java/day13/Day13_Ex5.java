import java.util.HashMap;

public class Day13_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——投票统计 + 获胜者
        String[] votes = { "张三", "李四", "张三", "王五", "张三", "李四" };
        HashMap<String, Integer> result = VoteUtil.countVotes(votes);
        System.out.println("张三票数：" + result.get("张三")); // 期望：张三票数：3
        System.out.println("获胜者：" + VoteUtil.winner(result)); // 期望：获胜者：张三
    }
}

// ===== 你的代码写在这里（类 VoteUtil：countVotes / winner 两个 static 方法）=====
class VoteUtil {
    public static HashMap<String, Integer> countVotes(String[] votes) {
        HashMap<String, Integer> result = new HashMap<>();
        for (String w : votes) {
            if (result.containsKey(w)) {
                result.put(w, result.get(w) + 1);
            }

            else {
                result.put(w, 1);
            }
        }
        return result;
    }

    public static String winner(HashMap<String, Integer> result) {
        int x = 0;
        String temp = "";
        for (String w : result.keySet()) {
            if (result.get(w) > x) {
                temp = w;
                x=result.get(w);
            }
        }
        return temp;
    }
}
// ===========================================