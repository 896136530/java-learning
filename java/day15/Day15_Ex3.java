public class Day15_Ex3 {
    public static void main(String[] args) {
        // 题 3：安全取数组——越界别崩
        String[] arr = {"a", "b"};
        System.out.println("取[1]：" + ArrayAt.safeGet(arr, 1));  // 期望：取[1]：b
        System.out.println("取[9]：" + ArrayAt.safeGet(arr, 9));  // 期望：取[9]：<越界>
    }
}

// ===== 你的代码写在这里（类 ArrayAt：static String safeGet(String[] arr, int i)，越界返回 "<越界>"）=====

// ===========================================