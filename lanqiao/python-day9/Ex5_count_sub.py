# 题 5：子串出现次数（不重叠）
# 输入：第一行字符串 s；第二行字符串 t
# 输出：一行 —— t 在 s 里出现了几次（不重叠计数）
# 要求：**写两遍**
#   第 1 遍：直接用 s.count(t) 一行解决
#   第 2 遍：用 find 循环手写一遍（把第 1 遍的结果注释掉，别删，留着对照）
#      提示：pos = s.find(t, start)；找到就 count += 1，然后 start = pos + len(t)；
#            找不到（返回 -1）就 break
# 自测：s="aaa" t="aa" → 1（⭐ 不重叠！）；s="aaaa" t="aa" → 2；
#       t 比 s 长 → 0；s 和 t 完全相同 → 1

# ===== 你的代码写在这里（第 1 遍：count 版）=====
s=input()
t=input()
print(s.count(t))
# ===========================

# ===== 第 2 遍：find 循环手写版 =====
m=input()
n=input()
count=0
start=0
while True:
    pos=m.find(n,start)
    if pos==-1:
        break
    else:
        count=count+1
        start=pos+len(n)
print(count)
# ===========================