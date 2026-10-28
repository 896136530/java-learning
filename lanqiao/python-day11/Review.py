# 🎯 复习：和不超过 S 的最长子数组（变长滑动窗口）⭐⭐
# 输入：第一行 n S；第二行 n 个整数（都是正整数，a[i] >= 1）
# 输出：第 1 行：满足"区间和 <= S"的最长连续子数组的长度（一个都没有 → 输出 0）
#       第 2 行：那个区间的左右端点（从 1 开始数，空格隔开）；
#                一个都没有 → 输出 0 0；若有多个等长 → 输出最靠左的那个
# 数据：1 <= n <= 100000，1 <= S <= 1e9，1 <= a[i] <= 1e9
# 要求：变长滑动窗口，O(n)：r 扩张、l 收缩；
#       不许用前缀和 + 双重循环枚举所有区间（O(n^2) 会超时）
# 提示：cur 是窗口 [l, r] 的和。cur > S 就 while 一直退 l（⚠️ 不是 if！）
#       退到合法之后，r - l + 1 就是"以 r 结尾的最长合法区间长度"
#       ⚠️ 数组全是正数，退 l 之后一定重新合法，滑动窗口才成立
#       ⚠️ "最靠左"：只在严格变长时才更新答案（用 > 不要用 >=）
# 自测：n = 1 且装得下、n = 1 且装不下、整个数组刚好不超、S 很大、并列时最靠左、
#       单个元素超过 S 但要继续往右找

# ===== 你的代码写在这里 =====
n,S=map(int,input().split())
a=list(map(int,input().split()))
cur=0
best_len=0
l=0
right=0
left=0
for i in range(n):
    cur+=a[i]
    while cur>S:
        cur-=a[l]
        l+=1
    if l<=i:
        window_len=i-l+1
        if window_len>best_len:
            best_len=window_len
            left=l
            right=i
print(best_len)
if best_len==0:
    print(0,0)
else:
    print(left+1,right+1)



# ===========================