# 🎯 蓝桥真题 3：矩阵查数（二维前缀和 + 容斥）⭐⭐⭐
# 输入：第一行 n m
#       接下来 n 行，每行 m 个整数（表格）
#       下一行 q
#       接下来 q 行，每行 x1 y1 x2 y2（1 <= x1 <= x2 <= n，1 <= y1 <= y2 <= m）
# 输出：q 行，每行一个整数 —— 对应查询的子矩阵和
# 数据：1 <= n, m <= 1000，0 <= 表格里的数 <= 1000，1 <= q <= 100000
# 要求：二维前缀和 O(1) 查询；四项容斥一个都不能少
# 提示：pre[i][j] = pre[i-1][j] + pre[i][j-1] - pre[i-1][j-1] + mat[i][j]
#       查询 sum = pre[x2][y2] - pre[x1-1][y2] - pre[x2][y1-1] + pre[x1-1][y1-1]

# ===== 你的代码写在这里 =====
n,m=map(int,input().split())
mat=[[0]*(m+1)]
for i in range(n):
    mat.append([0]+list(map(int,input().split())))
pre=[[0]*(m+1)for i in range(n+1)]
for i in range(1,n+1):
    for j in range(1,m+1):
        pre[i][j]=pre[i-1][j]+pre[i][j-1]-pre[i-1][j-1]+mat[i][j]
q=int(input())
for i in range(q):
    x1,y1,x2,y2=map(int,input().split())
    print(pre[x2][y2]-pre[x2][y1-1]-pre[x1-1][y2]+pre[x1-1][y1-1])


# ===========================

# ---- 本地自测：把题目里的「样例输入」粘到控制台跑，建议先在纸上画 pre 表 ----