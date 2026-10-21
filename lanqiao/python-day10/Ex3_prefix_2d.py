# 题 3：子矩阵求和（二维前缀和）
# 输入：第一行 n m；接下来 n 行每行 m 个整数；下一行 x1 y1 x2 y2
# 输出：一行一个整数 —— 该子矩阵所有数之和
# 要求：二维前缀和 + 四项容斥，不许两层循环暴力加
# 提示：pre[i][j] = pre[i-1][j] + pre[i][j-1] - pre[i-1][j-1] + mat[i][j]
#       ⚠️ 开数组用 [[0]*(m+1) for _ in range(n+1)]，不能用 [[0]*(m+1)]*(n+1)
# 自测：整个矩阵、单个格子、第一行 / 第一列（考验 pre[0][*]）

# ===== 你的代码写在这里 =====
n,m=map(int,input().split())
mat=[[0]*(m+1)]
for i in range(n):
    mat.append([0]+list(map(int,input().split())))
pre=[[0]*(m+1) for _ in range(n+1)]
for i in range(1,n+1):
    for j in range(1,m+1):
        pre[i][j]=pre[i-1][j]+pre[i][j-1]-pre[i-1][j-1]+mat[i][j]
x1,y1,x2,y2=map(int,input().split())
sum=pre[x2][y2]-pre[x1-1][y2]-pre[x2][y1-1]+pre[x1-1][y1-1]

print(sum)

# ===========================