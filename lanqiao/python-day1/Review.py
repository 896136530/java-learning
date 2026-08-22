# 🎯 复习：小超市结账（第一个"蓝桥杯风格"题目）
# 输入两件商品价格 + 付的钱 → 输出找零

# ===== 你的代码写在这里 =====
a, b = map(int, input().split())
pay = int(input())

total = a + b
change = pay - total
print(f"应找零：{change}元")
# ===========================