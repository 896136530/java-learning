# 题 3：求 1 到 n 的和
# 输入：一个整数 n
# 输出：一行，1 + 2 + ... + n 的总和
# 提示：while 或 for 都行；想清楚循环几遍、每次加的是谁

# ===== 你的代码写在这里 =====
'''
n=int(input("请输入一个数"))
i=1
sum=0
while i<=n:
    sum=sum+i
    i=i+1
print(sum)
'''
sum=0
n=int(input("请输入一个数"))
for i in range(1,n+1):
    sum=sum+i
print(sum)
# ===========================