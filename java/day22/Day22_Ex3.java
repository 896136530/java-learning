import java.util.*;

public class Day22_Ex3 {
    public static void main(String[] args) {
        // 题 3：TreeMap 按学号自动升序
        TreeMap<Integer, String> map = StudentMap.build();
        for (Map.Entry<Integer, String> e : map.entrySet()) {
            System.out.println(e.getKey() + " " + e.getValue());
        }
        // 期望输出：
        //   101 张三
        //   102 李四
        //   103 王五
    }
}

// ===== 你的代码写在这里：class StudentMap =====

// TODO：StudentMap.build() →
//   新建 TreeMap<Integer,String>，插入（顺序故意打乱）：
//     102 → "李四"   101 → "张三"   103 → "王五"
//   （TreeMap 会按键自动升序，main 打印出来就是 101/102/103）

// ===========================================