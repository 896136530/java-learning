import java.util.HashSet;

public class Day14_Ex4 {
    public static void main(String[] args) {
        // 题 4：找共同元素——HashSet 查"在不在"
        String[] a = {"猫", "狗", "鸟"};
        String[] b = {"狗", "鸟", "鱼"};
        System.out.println("共同：" + WordSet.common(a, b));  // 期望：共同：2
    }
}

// ===== 你的代码写在这里（类 WordSet：static int common(String[] a, String[] b)）=====
class WordSet{
    public static int common(String[]a,String[]b){
        HashSet<String>map=new HashSet<>();
        for(String w:a){
            map.add(w);
        }
        int count=0;
        for(String w:b){
            if(map.contains(w)){
                count=count+1;
            }
        }
        return count;
    }
}
// ===========================================