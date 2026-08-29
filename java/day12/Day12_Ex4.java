import java.util.ArrayList;

public class Day12_Ex4 {
    public static void main(String[] args) {
        // 题 4：删除所有等于 target 的元素——⚠️ 边遍历边删的大坑（必须从后往前删！）
        ArrayList<Integer> nums = new ArrayList<>();
        nums.add(1);
        nums.add(2);
        nums.add(3);
        nums.add(2);
        nums.add(4);
        nums.add(2);
        DedupeUtil.removeAll(nums, 2);
        System.out.println(nums); // 期望：[1, 3, 4]
    }
}

// ===== 你的代码写在这里（类 DedupeUtil：static void removeAll(ArrayList<Integer> nums, int target)）=====

// ===========================================