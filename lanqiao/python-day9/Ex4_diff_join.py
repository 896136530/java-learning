# 题 4：找出多出来的字符（Day8 题 3 的升级版）
# 输入：两行字符串，第一行长、第二行短
# 输出：一行 —— 第一行比第二行"多出来"的那些字符，按字典序从小到大拼成一串
#               （没有多出来的就输出一个空行）
# 提示：from collections import Counter
#       diff = Counter(第一行) - Counter(第二行)
#       ''.join(sorted(diff.elements()))   ← ⚠️ 一定要 join，直接 print 会打印出列表 ['b','c']
# 自测：aabcb / aab → bc；aabb / b → aab；abc / abc → 空行；baac / ab → ac

# ===== 你的代码写在这里 =====

# ===========================