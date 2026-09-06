# 第2章 线性表 —— Python 落地（演示脚本，非作业）
# 第一部分：顺序表 = Python 的 list（调库姿势 + 复杂度微实验）
# 第二部分：手写单链表（Node + LinkedList，体会"节点+指针"）

import time

# ================= 第一部分：list = 顺序表 =================
# 操作对照（背）：
#   lst[i]           O(1)   按下标取/改
#   lst.append(x)    O(1)   尾插（摊还）
#   lst.pop()        O(1)   尾删
#   lst.insert(i,x)  O(n)   中间插（后面的全挪）
#   lst.pop(i)       O(n)   中间删
#   x in lst         O(n)   找值

def benchmark():
    n = 20000
    t0 = time.perf_counter()
    a = []
    for i in range(n):
        a.append(i)              # 尾插 O(1)
    t1 = time.perf_counter()
    b = []
    for i in range(n):
        b.insert(0, i)           # 头插 O(n) → 总 O(n²)
    t2 = time.perf_counter()
    print(f"尾插 {n} 次: {(t1 - t0) * 1000:6.1f} ms")
    print(f"头插 {n} 次: {(t2 - t1) * 1000:6.1f} ms   ← 慢一两个数量级，这就是 O(n²) 的火")

# ================= 第二部分：手写单链表 =================

class Node:
    def __init__(self, val):
        self.val = val            # 数据
        self.next = None          # 指向下一个的"指针"

class LinkedList:
    def __init__(self):
        self.head = None          # 头指针

    def append(self, val):        # 尾插 O(n)：必须从头走到最后一个
        node = Node(val)
        if self.head is None:
            self.head = node
            return
        cur = self.head
        while cur.next:
            cur = cur.next
        cur.next = node

    def insert_front(self, val):  # 头插 O(1)：只改头指针，不用走
        node = Node(val)
        node.next = self.head
        self.head = node

    def display(self):
        out = []
        cur = self.head
        while cur:
            out.append(str(cur.val))
            cur = cur.next
        print(" -> ".join(out))

if __name__ == "__main__":
    print("= 一、顺序表(list) 复杂度微实验 =")
    benchmark()
    print()
    print("= 二、手写单链表 =")
    ll = LinkedList()
    for v in [1, 2, 3]:
        ll.append(v)          # 尾插：1 -> 2 -> 3
    ll.insert_front(0)        # 头插：0 -> 1 -> 2 -> 3
    ll.display()
    # 链表没有下标！想拿第 3 个元素，只能从头走 2 步 → O(n)
    cur = ll.head
    for _ in range(2):
        cur = cur.next
    print("链表的第 3 个元素（从头走 2 步）:", cur.val)
    # 头插法建表是反序的：依次头插 7,8,9 → 9 -> 8 -> 7
    rev = LinkedList()
    for v in [7, 8, 9]:
        rev.insert_front(v)
    print("头插法依次插 7,8,9 的结果:", end=" ")
    rev.display()
    print("（和输入顺序相反 = 头插法的经典特征）")