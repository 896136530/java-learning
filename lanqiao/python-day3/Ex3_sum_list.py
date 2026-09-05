# 题 3：一行数求和
# 输入：一行若干个整数（空格隔开）
# 输出：一行，它们的总和

# ===== 你的代码写在这里 =====
nums=list(map(int,input().split()))
sum=0
for i in nums:
    sum=sum+i
print(sum)
# ===========================