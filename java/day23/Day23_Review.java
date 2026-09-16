import java.util.*;

public class Day23_Review {
    public static void main(String[] args) {
        // 综合：学生成绩排行榜系统
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("张三", 88);
        scores.put("李四", 95);
        scores.put("王五", 76);
        scores.put("赵六", 60);
        scores.put("钱七", 100);

        RankBoard.printTop3(scores);
        System.out.println("平均分：" + RankBoard.average(scores));

        // 期望输出：
        //   第1名：钱七 100
        //   第2名：李四 95
        //   第3名：张三 88
        //   平均分：83.80
    }
}

// ===== 你的代码写在这里：class RankBoard =====

// TODO：class RankBoard（两个 static 方法）
//   ① printTop3(Map<String, Integer> scores)
//      · 把表倒进列表：List<Map.Entry<String,Integer>> list = new ArrayList<>(scores.entrySet());
//      · 按成绩降序排序：list.sort(...)（用 Map.Entry 的 getValue() 拿分数）
//      · 打印前 3 名，格式：第1名：钱七 100   （注意是全角冒号！）
//   ② average(Map<String, Integer> scores)
//      · 遍历 values() 求和，先转 double 再除人数
//      · 返回 String.format("%.2f", avg)（结果必须是 "83.80"）

// ⚠️ 千万别把"成绩"当 key 用 TreeMap 排——同分会互相覆盖，人会消失！
class RankBoard{
    public static void printTop3(Map<String,Integer>scores){
        List<Map.Entry<String,Integer>>list=new ArrayList<>(scores.entrySet());
        list.sort((o1,o2)->Integer.compare(o2.getValue(),o1.getValue()));
        for(int i=0;i<Math.min(3,list.size());i++){
            System.out.println("第"+(i+1)+"名："+list.get(i).getKey()+" "+list.get(i).getValue());
        }
    }
    public static String average(Map<String,Integer>scores){
        double sum=0;
        for(int i:scores.values()){
            sum+=i;
        }
        double avg=sum/scores.size();
        return String.format("%.2f",avg);
    }
}
// ===========================================
