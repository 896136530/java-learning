# 题 2：每一行之和
# 输入：第一行两个整数 n m；接下来 n 行，每行 m 个整数
# 输出：n 行，第 i 行是矩阵第 i 行元素之和
# 提示：外层走「行」，内层走「列」；每换一行，累加变量要清零
# 自测：2 3 的矩阵应该输出 2 行

# ===== 你的代码写在这里 =====
n,m=map(int,input().split())

matrix=[]
for i in range(n):
    row=list(map(int,input().split()))
    matrix.append(row)
for j in range(n):
    sum=0
    for k in range(m):
        sum=sum+matrix[j][k]
    print(sum)
# ===========================
