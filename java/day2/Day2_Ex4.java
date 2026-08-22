public class Day2_Ex4 {
    public static void main(String[] args) {
        // 题 4：用 while 循环计算 1+2+3+...+100 的和并打印（结果 5050）
        // 挑战：再计算 1~100 之间所有偶数的和（结果 2550）
        // 提示：int i = 1; while (i <= 100) { sum += i; i++; }

        // ===== 你的代码写在这里 =====
int i=0;
int sum=0;
while(i<=100){
sum += i;
i++;
}
System.out.println(sum);
int sum1=0;
int x=0;
while(x<=100){
    if(x%2==0){
sum1 += x ;}
    
x++;    
}
System.out.println(sum1);
        // ===========================
    }
}
