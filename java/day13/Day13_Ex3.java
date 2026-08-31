import java.util.HashMap;

public class Day13_Ex3 {
    public static void main(String[] args) {
        // 题 3：改工资——put 同键 = 覆盖
        HashMap<String, Integer> salary = new HashMap<>();
        salary.put("小明", 5000);
        salary.put("小红", 6000);
        System.out.println("小明新工资：" + Wage.raise(salary, "小明", 500));  // 期望：小明新工资：5500
        System.out.println("小红工资：" + salary.get("小红"));                 // 期望：小红工资：6000
        System.out.println("总人数：" + salary.size());                        // 期望：总人数：2
    }
}

// ===== 你的代码写在这里（类 Wage：static int raise(HashMap<String,Integer> map, String name, int amount)）=====
class Wage{
    public static int raise(HashMap<String,Integer>map,String name,int amount){
        int x=(map.get(name)+amount);
        map.put(name,x);
        return  map.get(name);
    }
}
// ===========================================