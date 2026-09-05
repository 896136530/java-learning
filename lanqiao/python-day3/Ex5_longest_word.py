# 题 5：找出最长的单词
# 输入：一行英文句子（单词间空格隔开）
# 输出：一行，最长的那个单词（并列最长时输出第一个）

# ===== 你的代码写在这里 =====
line=input()
words=line.split()
max=len(words[0])
maxwords=words[0]
for s in words:
    if len(s)>max:
        maxwords=s
        max=len(s)
print(maxwords)
# ===========================