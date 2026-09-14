# 题 2：阶乘函数
# 输入：一行，一个整数 n（0 ≤ n ≤ 20）
# 输出：一行，n!

# ===== 你的代码写在这里 =====
def chengfa(n):
    sum=1
    for i in range(1,n+1):
        sum=sum*i
    return sum
n=int(input(""))
print(chengfa(n))
# ===========================