# 题 5：两条对角线之和（方阵）
# 输入：第一行一个整数 n（方阵边长）；接下来 n 行，每行 n 个整数
# 输出：一行两个数：主对角线之和 副对角线之和
# 提示：主对角线 m[i][i]；副对角线 m[i][n-1-i]（倒着数的下标）
# 自测：n=1 时两条对角线是同一个元素，两个和应该相等

# ===== 你的代码写在这里 =====
n=int(input())
matrix=[]
sum1=0
sum2=0
for i in range(n):
    row=list(map(int,input().split()))
    matrix.append(row)
for i in range(n):
    for j in range(n):
        if j==i:
            sum1=sum1+matrix[i][i]
for i in range(n):
    for j in range(n):
        if j==n-1-i:
            sum2=sum2+matrix[i][j]
print(sum1,sum2)

# ===========================
