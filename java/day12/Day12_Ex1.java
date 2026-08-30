import java.util.ArrayList;

public class Day12_Ex1 {
    public static void main(String[] args) {
        // 题 1：ArrayList 求和——遍历 + get
        ArrayList<Integer> nums = new ArrayList<>();
        nums.add(3);
        nums.add(5);
        nums.add(7);
        System.out.println("总和：" + SumUtil.sum(nums));   // 期望：总和：15
        ArrayList<Integer> empty = new ArrayList<>();
        System.out.println("空表：" + SumUtil.sum(empty));  // 期望：空表：0
    }
}

// ===== 你的代码写在这里（类 SumUtil：static int sum(ArrayList<Integer> nums)）=====
class SumUtil{
    public static int sum(ArrayList<Integer>nums){
        int count=0;
        for(int i=0;i<nums.size();i++){
            count=count+nums.get(i);
        }
        return count;
    }
}
// ===========================================