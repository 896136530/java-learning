# 🎯 复习：成绩表函数版（参考答案 —— 你自己写完之后再看当复习）
# 输入：第一行整数 n；接下来 n 行「姓名 分数」
# 输出：一行，「姓名 分数」（最高分者，并列取第一个）
#
# 思路三步：① 写函数 get_highest(rows) ② 主流程读 n 行造元组列表 ③ 调函数并打印
# 考点：def / 参数 / return / 元组 / 列表 / 打擂台找最值

# ===== ① 函数区：把"找最高分"这件事打包成一个零件 =====

def get_highest(rows):
    """rows 是 [(姓名, 分数), ...]，返回分数最高的那个元组（并列取第一个）"""
    best = rows[0]                # 打擂台：先假设第一个是冠军（Day2 学的思路）
    for row in rows:
        if row[1] > best[1]:      # 只比分数：row[1] 是分数、row[0] 是姓名
            best = row            # 有人更高，换擂主
    return best                   # 返回整个元组，例如 ("李四", 95)


# ===== ② 主流程：读 → 调 → 打 =====

n = int(input())                  # 第一行：几个人
rows = []                         # 准备一个空列表装元组
for _ in range(n):                # 循环 n 次
    name, score = input().split() # 一行拆成两块："李四 95" → "李四"、"95"
    rows.append((name, int(score)))   # 打包成元组塞进列表 → ("李四", 95)

best_name, best_score = get_highest(rows)   # 元组解包，一次拿两个值
print(best_name, best_score)                # print 两个值会自动加空格 → 李四 95

# ===== 三个容易踩的点 =====
# 1) input().split() 拿到的是字符串，分数必须 int() 转一下，否则比大小会按字典序比
# 2) rows.append((name, int(score))) 里的双括号：外层是 append 的括号，内层才是元组
# 3) 用 > 而不是 >= ：并列时保留先出现的那个（题目要求"并列取第一个"）
#
# 自测：输入
#   2
#   张三 88
#   李四 95
# 输出应为：李四 95
