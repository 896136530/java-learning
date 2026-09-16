import java.util.*;

public class Day23_Ex5 {
    public static void main(String[] args) {
        // 题 5：分数段统计（Map 计数套路）
        int[] scores = {95, 88, 76, 100, 60, 45};

        Map<String, Integer> result = StatUtil.byLevel(scores);
        String[] levels = {"优秀", "良好", "及格", "不及格"};
        for (String lv : levels) {
            System.out.println(lv + " " + result.getOrDefault(lv, 0) + " 人");
        }
        // 期望输出：
        //   优秀 2 人
        //   良好 1 人
        //   及格 2 人
        //   不及格 1 人
    }
}

// ===== 你的代码写在这里：class StatUtil =====

// TODO：static Map<String, Integer> byLevel(int[] scores)
//   ① 新建 Map<String, Integer> map = new HashMap<>();
//   ② 遍历 scores，按分数判档位：
//        >= 90 → "优秀"   80~89 → "良好"   60~79 → "及格"   其余 → "不及格"
//   ③ 计数套路：map.put(level, map.getOrDefault(level, 0) + 1);
//   ④ 返
class StatUtil{
    public static Map<String ,Integer>byLevel(int[] scores){
        Map<String ,Integer>map=new HashMap<>();
        for(int i:scores){
            if(i>=90){
                map.put("优秀",map.getOrDefault("优秀",0)+1);
            } 
            else if(i>=80){
                map.put("良好",map.getOrDefault("良好",0)+1);
            }
            else if(i>=60){
                map.put("及格",map.getOrDefault("及格",0)+1);
            }
            else{
                map.put("不及格",map.getOrDefault("不及格",0)+1);
            }
        }
        return map;
    }
}
// ===========================================
