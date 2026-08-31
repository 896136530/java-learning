import java.util.HashMap;

public class Day13_Ex1 {
    public static void main(String[] args) {
        // 题 1：查表——get 查价格，containsKey 判断有没有
        HashMap<String, Integer> fruit = new HashMap<>();
        fruit.put("苹果", 3);
        fruit.put("香蕉", 2);
        System.out.println("苹果 " + PriceMap.get(fruit, "苹果") + " 元");      // 期望：苹果 3 元
        System.out.println("西瓜 " + PriceMap.get(fruit, "西瓜") + " 元");      // 期望：西瓜 null 元（查不到返回 null）
        System.out.println("有香蕉？" + PriceMap.has(fruit, "香蕉"));           // 期望：有香蕉？true
    }
}

// ===== 你的代码写在这里（类 PriceMap：get / has 两个 static 方法）=====
class PriceMap{
    public static Integer get(HashMap<String ,Integer>fruit,String key){
        return fruit .get(key);
    }
    public static boolean has(HashMap<String,Integer>fruit,String key){
        return fruit.containsKey(key);
    } 
}
// ===========================================