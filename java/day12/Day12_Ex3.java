import java.util.ArrayList;

public class Day12_Ex3 {
    public static void main(String[] args) {
        // 题 3：遍历找最长——打擂法
        ArrayList<String> words = new ArrayList<>();
        words.add("Java");
        words.add("Python");
        words.add("C++");
        System.out.println("最长的：" + WordUtil.longest(words)); // 期望：Python
        ArrayList<String> words2 = new ArrayList<>();
        words2.add("a");
        words2.add("bb");
        System.out.println("最长的：" + WordUtil.longest(words2)); // 期望：bb
    }
}

// ===== 你的代码写在这里（类 WordUtil：static String longest(ArrayList<String>
// words)）=====
class WordUtil {
    public static String longest(ArrayList<String> words) {
        String longWord = words.get(0);
        int longLength = longWord.length();
        for (int i = 0; i < words.size(); i++) {
            String temp = words.get(i);
            int tempLength = temp.length();
            if (tempLength > longLength) {
                longWord = temp;
            }
        }
        return longWord;
    }
}
// ===========================================