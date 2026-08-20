public class Day2_Ex1 {
    public static void main(String[] args) {
        // 题 1：闰年判断
        // 规则：能被 4 整除且不能被 100 整除，或者能被 400 整除
        int year = 2024;   // 试试改成 1900 / 2000 / 2025 再运行

        // ===== 你的代码写在这里 =====
        // 输出："2024 是闰年" 或 "2024 不是闰年"
if(year%4==0&&year%100!=0||year%400==0){
System.out.println(year+"是闰年");
    }
    else {
        
        System.out.println(year+"不是闰年");
    }}
}
