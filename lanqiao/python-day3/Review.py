# 🎯 复习：学生成绩表找最高分
# 输入：
#   第一行：整数 n（几个学生）
#   接下来 n 行：每行 姓名 分数（空格隔开）
# 输出：一行，分数最高者的 姓名 分数

# ===== 你的代码写在这里 =====
n=int(input())
nums=[]
name=[]
for i in range(n):
    line=input()
    words=line.split()
    name.append(words[0])
    nums.append(int(words[1]))
max_nums=max(nums)
print(name[nums.index(max_nums)],max_nums)
# ===========================