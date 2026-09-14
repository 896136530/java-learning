# 题 5：加减乘除计算器
# 输入：一行，「数1 运算符 数2」（空格隔开，运算符为 + - * / 之一）
# 输出：一行，运算结果（除法用 /，可能带小数）

# ===== 你的代码写在这里 =====
def calc(num1,operator,num2):
    if operator=="+":
        return num1+num2
    elif operator=="-":
        return num1-num2
    elif operator=="*":
        return num1*num2
    elif operator=="/":
        return num1/num2
    else:
        return "输入错误"
n=input("")
num1,operator,num2=n.split()
num1=int(num1)
num2=int(num2)
print(calc(num1,operator,num2))
# ===========================