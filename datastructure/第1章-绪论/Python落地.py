# 第1章 绪论 —— Python 落地：让"时间复杂度"看得见

# 这是演示脚本不是作业：跑一遍，亲眼看 O(1) 和 O(n²) 差多少。
# 运行：python 第1章_复杂度实验.py

import time

N = 100_000  # 十万，蓝桥里很常见的规模


def o1(n):
    """O(1)：一步算完"""
    return n * (n + 1) // 2          # 等差公式，不循环


def on(n):
    """O(n)：循环 n 次"""
    s = 0
    for i in range(1, n + 1):
        s += i
    return s


def on2(n):
    """O(n^2)：双层嵌套，n=10万 时就是 100亿 次——别真跑完，只数循环次数"""
    cnt = 0
    for i in range(n):
        for j in range(n):
            cnt += 1
            if cnt >= 1_000_000:     # 数到一百万就停，不然等到天黑
                return cnt
    return cnt


def ologn(n):
    """O(log n)：每次砍半，10万 只要 17 步"""
    step = 0
    while n > 1:
        n //= 2
        step += 1
    return step


print(f"N = {N:,}\n")

t = time.time()
print(f"O(1)   求和        = {o1(N):>12,}    耗时 {time.time()-t:.6f}s")

t = time.time()
print(f"O(n)   求和        = {on(N):>12,}    耗时 {time.time()-t:.6f}s")

t = time.time()
cnt = on2(N)
print(f"O(n^2) 数到100万   = {cnt:>12,}    耗时 {time.time()-t:.6f}s")

t = time.time()
step = ologn(N)
print(f"O(logn) 砍半次数   = {step:>12,}    耗时 {time.time()-t:.6f}s")

print()
print("结论：同样的 n=10万 ——")
print("  O(1) 和 O(n) 都在一秒内；O(n^2) 数到 100 万就要这么久；")
print("  O(log n) 只要 17 步。这就是蓝桥里为什么 n 大时要选低阶算法。")