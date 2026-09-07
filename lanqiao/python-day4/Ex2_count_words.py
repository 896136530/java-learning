# 题 2：词频统计
# 输入：一行英文句子（单词间空格隔开，可能有重复）
# 输出：每个单词出现次数，每行「单词 次数」，按第一次出现的顺序

# ===== 你的代码写在这里 =====
line=input("")
words=line.split()
d={}
for word in words:
    d[word]=d.get(word,0)+1
for k, v in d.items():
    print(k, v)
# ===========================