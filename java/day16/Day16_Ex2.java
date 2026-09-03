public class Day16_Ex2 {
    public static void main(String[] args) {
        // 题 2：双泛型对——K 和 V 各管各的
        Pair<String, Integer> p = new Pair<>();
        p.set("小明", 18);
        System.out.println(p.getKey());    // 期望：小明
        System.out.println(p.getValue());  // 期望：18
    }
}

// ===== 你的代码写在这里（类 Pair<K,V>：set / getKey / getValue）=====

// ===========================================