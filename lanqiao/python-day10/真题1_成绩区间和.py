# 🎯 蓝桥真题 1：成绩区间和（前缀和）
# 输入：第一行 n q
#       第二行 n 个整数（成绩 a[1] ~ a[n]）
#       接下来 q 行，每行 l r（1 <= l <= r <= n）
# 输出：q 行，每行一个整数 —— 对应询问的区间和
# 数据：1 <= n, q <= 100000，0 <= a[i] <= 100
# 要求：每次询问 O(1)；用暴力 sum 会超时
# 提示：pre = [0] + list(accumulate(a))；答案是 pre[r] - pre[l-1]

# ===== 你的代码写在这里 =====
n,q=map(int,input().split())
a=list(map(int,input().split()))
pre=[0]*(n+1)
for i in range(1,n+1):
    pre[i]=pre[i-1]+a[i-1]
for i in range(q):
    l,r=map(int,input().split())
    print(pre[r]-pre[l-1])

# ===========================

# ---- 本地自测：把题目里的「样例输入」粘到控制台跑，自己手算答案对一遍 ----