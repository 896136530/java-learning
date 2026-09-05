# 题 4：从大到小排序输出
# 输入：一行若干个整数（空格隔开）
# 输出：一行，从大到小排序，数字间一个空格（结尾不要多余空格）

# ===== 你的代码写在这里 =====
nums=list(map(int,input().split()))
s=sorted(nums,reverse=True)
for i in s:
    print(i,end=" ")
# ===========================