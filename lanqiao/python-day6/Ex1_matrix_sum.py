# 题 1：矩阵所有元素之和
# 输入：第一行两个整数 n m；接下来 n 行，每行 m 个整数
# 输出：一行，所有元素的总和
# 提示：先写「读矩阵」三行骨架，再双重循环累加
# 自测：别忘了试 1 1 的单元素矩阵、以及带负数的矩阵

# ===== 你的代码写在这里 =====
n,m=map(int,input().split())
sum=0
matrix=[]
for i in range(n):
    row=list(map(int,input().split()))
    matrix.append(row)
for j in range(n):
    for k in range(m):
        sum=sum+matrix[j][k]
print(sum)
# ===========================
