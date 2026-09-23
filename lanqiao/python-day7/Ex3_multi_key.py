# 题 3：多关键字排序（今天的核心）
# 输入：第一行一个整数 n；接下来 n 行，每行「姓名 分数」
# 输出：n 行 —— 分数从高到低；分数相同时，姓名从小到大
# 提示：一个元组 key 同时管两件事：key=lambda x: (-x[1], x[0])
#      负号让「分数」变降序，姓名保持升序（reverse=True 会把两个一起反，不行）
# 自测：造一组「三人里两人同分」的数据，检查同分那两人是否按姓名排好了

# ===== 你的代码写在这里 =====
n=int(input())
x=[]
for i in range(n):
    name,score=input().split()
    x.append((name,int(score)))
x.sort(key=lambda x:(-x[1],x[0]))
for name,score in x:
    print(name,score)
# ===========================
