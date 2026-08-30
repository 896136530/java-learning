import java.util.ArrayList;

public class Day12_Ex5 {
    public static void main(String[] args) {
        // 题 5：综合——最高分 + 不及格全捞到 60（get/set 一遍过）
        ArrayList<Integer> scores = new ArrayList<>();
        scores.add(45);
        scores.add(80);
        scores.add(59);
        scores.add(100);
        System.out.println("最高分：" + GradeUtil.top(scores));  // 期望：100
        GradeUtil.raise(scores);
        System.out.println("修改后：" + scores);                 // 期望：[60, 80, 60, 100]
    }
}

// ===== 你的代码写在这里（类 GradeUtil：top / raise 两个 static 方法）=====
class GradeUtil{
    public static int top(ArrayList<Integer>scores){
       int temp=scores.get(0);
       for(int i=0;i<scores.size();i++){
        if(scores.get(i)>temp){
            temp=scores.get(i);
        }
       }
       return temp;
    }
    public static void raise(ArrayList<Integer>scores){
        for(int i=0;i<scores.size();i++){
            if(scores.get(i)<60){

                scores.set(i,60);
            }
        }
    }
}
// ===========================================