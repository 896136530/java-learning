public class Day16_Ex3 {
    public static void main(String[] args) {
        // 题 3：泛型方法——同一个方法，类型随便换
        String[] words = {"a", "b"};
        System.out.println(FirstEl.first(words));   // 期望：a

        Integer[] nums = {5, 6};
        System.out.println(FirstEl.first(nums));    // 期望：5
    }
}

// ===== 你的代码写在这里（类 FirstEl：static <T> T first(T[] arr)）=====
class FirstEl{
    public static <T> T first(T[] arr){
        return arr[0];
    }
}
// ===========================================