# 题 3：最大值及其位置
# 输入：第一行两个整数 n m；接下来 n 行，每行 m 个整数
# 输出：一行三个数：最大值 行号 列号（行号列号从 1 开始数！）
# 提示：打擂台——先假设 m[0][0] 最大；发现有更大的，同时记下行、列
# 自测：最大值在左上角 / 右下角，各测一次

# ===== 你的代码写在这里 =====
n,m=map(int,input().split())
matrix=[]
for i in range(n):
    row=list(map(int,input().split()))
    matrix.append(row)
max_nums=matrix[0][0]
max_i=0
max_j=0
for i in range(n):
    for j in range(m):
        if matrix[i][j]>max_nums:
            max_nums=matrix[i][j]
            max_i=i
            max_j=j
print(max_nums,max_i+1,max_j+1)
# ===========================