public class Day2_Ex5 {
    public static void main(String[] args) {
        // 题 5：成绩等级（switch 或 if 都行）
        // 规则：90~100→A  80~89→B  70~79→C  60~69→D  0~59→E
        int score = 85;
        // 提示（switch 思路）：score / 10 会把 90~99 变成 9，80~89 变成 8 ...
        // 输出："85 分对应等级 B"

        // ===== 你的代码写在这里 =====
if(score>=90&&score<=100){
    System.out.println(score+"分对应等级A");
}else if(score>=80&&score<=89){ 
    System.out.println(score+"分对应等级B");
}else if(score>=70&&score<=79){
    System.out.println(score+"分对应等级C");
}else if(score>=60&&score<=69){
    System.out.println(score+"分对应等级D");
}else{
    System.out.println(score+"分对应等级E");                    
}

        // ===========================
    }
}
