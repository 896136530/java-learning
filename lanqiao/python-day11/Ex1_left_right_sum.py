# 题 1：有序数组两数之和（左右指针）
# 输入：第一行 n target；第二行 n 个整数（数组 a，保证非递减）
# 输出：一行 —— 存在两个不同位置的数之和等于 target → 输出这两个下标（从 1 开始数，小的在前，空格隔开）
#                不存在 → 输出 -1
# 数据：2 <= n <= 100000，|a[i]| <= 1e9；保证最多只有一组解
# 要求：用左右指针（l = 0, r = n - 1，每次比较淘汰一个元素），复杂度 O(n)；
#       不许双重循环 for i: for j:（O(n^2) 会超时）
# 提示：a[l] + a[r] > target 就把 r 往左收，< target 就把 l 往右推
#       循环条件是 while l < r（不是 l <= r）
# 自测：答案是最左一对、最右一对、无解、有重复数字、n = 2

# ===== 你的代码写在这里 =====
n,target=map(int,input().split())
a=list(map(int,input().split()))
l,r=0,n-1
while l<r:
    if a[l]+a[r]==target:
        print(l+1,r+1)
        break
    
    elif a[l]+a[r]>target:
        r-=1
    else:
        l+=1
else:
    print(-1)

    
    



# ===========================