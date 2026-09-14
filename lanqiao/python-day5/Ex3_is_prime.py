# 题 3：素数判断函数
# 输入：一行，一个整数 n（2 ≤ n ≤ 10⁶）
# 输出：一行，「是素数」或「不是素数」

# ===== 你的代码写在这里 =====
def sushu(n):
    if(n<2):
        return False
    i=int(n**0.5)
    for m in range(2,i+1):
        if(n%m==0):
            return False
    return True
n=int(input())
print("是素数" if sushu(n) else "不是素数")
# ===========================
