import java.util.*;

public class Day22_Ex1 {
    public static void main(String[] args) {
        // 题 1：HashMap 数水果，统计 apple 出现几次
        String sentence = "apple banana apple orange apple banana";
        int n = FruitCounter.count(sentence, "apple");
        System.out.println("apple 出现 " + n + " 次");   // 期望输出：apple 出现 3 次
    }
}

// ===== 你的代码写在这里：class FruitCounter =====

// TODO：FruitCounter.count(sentence, word) →
//   1) Map<String,Integer> 统计 sentence.split(" ") 每个单词出现次数
//   2) 返回 word 出现的次数（没出现过返回 0）

// ===========================================