import java.util.HashMap;

public class Day13_Ex4 {
    public static void main(String[] args) {
        // 题 4：过滤及格表——keySet 遍历，合格的进新表
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("小明", 90);
        scores.put("小红", 55);
        scores.put("小刚", 70);
        HashMap<String, Integer> pass = Filter.keepPass(scores, 60);
        System.out.println("小明 在名单？" + pass.containsKey("小明"));  // 期望：小明 在名单？true
        System.out.println("小红 在名单？" + pass.containsKey("小红"));  // 期望：小红 在名单？false
        System.out.println("通过人数：" + pass.size());                 // 期望：通过人数：2
    }
}

// ===== 你的代码写在这里（类 Filter：static HashMap<String,Integer> keepPass(HashMap<String,Integer> map, int min)）=====

// ===========================================