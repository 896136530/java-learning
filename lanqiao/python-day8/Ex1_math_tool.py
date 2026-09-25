# 题 1：数学小工具（math 库）
# 输入：一行，两个正整数 a b（空格隔开）
# 输出：三行
#   第 1 行：最大公约数
#   第 2 行：最小公倍数
#   第 3 行：两数之和是不是完全平方数（是 → True，不是 → False）
# 提示：from math import gcd, lcm, isqrt
#       判断平方数用 isqrt(n)**2 == n（别用 sqrt，有浮点误差）
# 自测：a=b、a 是 b 的倍数、和正好是平方数（比如 9+16=25）各测一组

# ===== 你的代码写在这里 =====
import math
a,b=map(int,input().split())
print(math.gcd(a,b))
print(math.lcm(a,b))
print(math.isqrt(a+b)**2==a+b)
# ===========================