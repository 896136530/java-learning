# 题 5：成绩字典
# 输入：第一行整数 n；接下来 n 行「姓名 分数」；最后一行一个要查的名字
# 输出：一行，他的分数；查不到输出"没有此人"

# ===== 你的代码写在这里 =====
n=int(input())
d={}
for n in range(0,n):
    words=input().split()
    d[words[0]]=words[1]
chaxun=input()
print(d.get(chaxun,"没有此人"))
# ===========================