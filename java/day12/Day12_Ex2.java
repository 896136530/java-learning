import java.util.ArrayList;

public class Day12_Ex2 {
    public static void main(String[] args) {
        // 题 2：增删查——lastItem 取最后一个，removeAt 删除并返回被删的
        ArrayList<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        list.add("C");
        System.out.println("最后一个：" + EditUtil.lastItem(list));     // 期望：C
        System.out.println("删掉的：" + EditUtil.removeAt(list, 1));    // 期望：Python
        System.out.println("删除后还有 " + list.size() + " 个");        // 期望：2
    }
}

// ===== 你的代码写在这里（类 EditUtil：lastItem / removeAt 两个 static 方法）=====
class EditUtil{
    public static String lastItem(ArrayList<String>list){
        int size=list.size();
        return list.remove(size-1);
    }
    public static String removeAt(ArrayList<String>list,int x){
return list.get(x);
    }
}
// ===========================================