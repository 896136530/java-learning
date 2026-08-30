import java.util.HashMap;

public class Day13_Ex2 {
    public static void main(String[] args) {
        // 题 2：单词统计——containsKey 模板（刷题高频）
        String[] words = {"apple", "banana", "apple", "orange", "apple"};
        HashMap<String, Integer> count = Counter.countWords(words);
        System.out.println("apple 出现 " + count.get("apple") + " 次");      // 期望：apple 出现 3 次
        System.out.println("banana 出现 " + count.get("banana") + " 次");    // 期望：banana 出现 1 次
        System.out.println("grape 出现 " + count.get("grape") + " 次");      // 期望：grape 出现 null 次
    }
}

// ===== 你的代码写在这里（类 Counter：static HashMap<String,Integer> countWords(String[] words)）=====

// ===========================================