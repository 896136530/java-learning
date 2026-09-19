# 📒 Step 3 标准答案（持久化）

> 本答案**本机实测通过**：两轮运行（第一轮记 3 笔保存 → 第二轮重开账还在）+ 脏文件容错 + 乱输入压测。
> 用法：做完自己的版本再看，或者卡住时对照某一段。

---

## TODO A：启动时读账本

```java
static void loadBills(ArrayList<Bill> bills) {
    if (!new File(FILE).exists()) {
        return;   // 第一次运行：没有文件 = 空账本
    }
    try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;                       // 空行跳过
            }
            String[] parts = line.split("\t");
            if (parts.length < 3) {
                continue;                       // 缺字段的坏行跳过
            }
            try {
                bills.add(new Bill(parts[0], Integer.parseInt(parts[1]), parts[2]));
            } catch (NumberFormatException e) {
                // 金额坏了就跳过这一行——不要因为一行脏数据丢掉整个账本
            }
        }
    } catch (IOException e) {
        System.out.println("读取账本失败，先当空账本：" + e.getMessage());
    }
}
```

**实测**：文件不存在 → 不崩、菜单 5 输出全 0；文件存在 → 3 笔明细原样回来。

- 易错 ①：忘写 `new File(FILE).exists()` 判断 → 第一次运行 `FileNotFoundException` 崩
- 易错 ②：`while ((line = br.readLine()) != null)` 写成 `while (br.readLine() != null)` → **每读一行丢一行**（条件里读掉的那行没被处理，经典 bug）
- 易错 ③：`split("\t")` 写成 `split(" ")` → 备注"公交 月卡 充值"被拆成 5 段，金额位置错乱
- 加分理解：**一行的坏数据不该毁掉整个文件**。真实项目里读外部数据一律"逐行容错"，这条 `try-catch` 就是那个意思

**脏文件容错实测**：往 `账本.txt` 里塞了空行 + 只有两段的行 + 金额是 `abc` 的行 → 程序**只认出 2 笔有效账**，其余静默跳过，不崩 ✅

## TODO B：记一笔

```java
System.out.print("分类（吃饭/交通/娱乐/收入）：");
String cat = sc.nextLine();

int amount;
while (true) {
    try {
        amount = Integer.parseInt(sc.nextLine());
        break;
    } catch (NumberFormatException e) {
        System.out.println("金额必须是整数，请重新输入");
    }
}

System.out.print("备注（一句话，比如 食堂午饭）：");
String note = sc.nextLine();

bills.add(new Bill(cat, amount, note));
refresh(bills, map);
System.out.println("已记一笔！");
```

**实测**：金额输 `abc` → 提示重来；输 `-100` 通过；备注带空格正常。

- ⚠️ `int amount;` **别写 `int amount = 0;`** —— 那样"金额是 0"和"用户还没输"分不清。用"先声明、try 里赋值、成功才 break"的写法，编译器也能确认它一定被赋过值

## TODO C：删除一笔

```java
if (bills.isEmpty()) {
    System.out.println("还没有明细，先记一笔吧");
    continue;
}
System.out.print("请输入序号（1~" + bills.size() + "）：");
int index;
try {
    index = Integer.parseInt(sc.nextLine());
} catch (NumberFormatException e) {
    System.out.println("请输入数字！");
    continue;
}
if (index < 1 || index > bills.size()) {
    System.out.println("序号无效");
    continue;
}
bills.remove(index - 1);
refresh(bills, map);          // ← 删完必须重算汇总
System.out.println("已删除");
```

**实测**：序号 `abc` → "请输入数字！"；序号 `9` → "序号无效"；序号 `1` → "已删除"且明细少一条、汇总同步变。

- 易错 ①：忘了 `refresh` → 明细删了，**菜单 2 的汇总还是老的**（数据不一致，真实项目里这种 bug 最难查）
- 易错 ②：`remove(index)` 忘了 `-1` → 删错人（序号 1 删掉第 2 条），或者序号 = size 时越界

## TODO D：查看总账

```java
int income = 0;
int expense = 0;
for (Bill b : bills) {
    if (b.amount >= 0) {
        income += b.amount;
    } else {
        expense += b.amount;
    }
}
System.out.println("总收入：" + income);
System.out.println("总支出：" + expense);
System.out.println("结余：" + (income + expense));
```

**实测输出**：
```
总收入：100
总支出：-20
结余：80
```

- 易错 ①：`expense += Math.abs(b.amount)` → 总支出变成 `20`、结余算成 `100+20=120`（**取绝对值就错了**）
- 易错 ②：`b.amount >= 0` 写成 `> 0` → 金额为 0 的那笔被算进支出（虽然结果一样，但语义不对）
- 加分理解：空账本时输出 `总收入：0 / 总支出：0 / 结余：0`，不用特判"还没有账"

## TODO E：退出并保存

```java
if (bills.isEmpty()) {
    System.out.println("再见！");
    break;
}
saveBills(bills);
System.out.println("已保存 " + bills.size() + " 笔账，再见！");
break;
```

**实测**：`已保存 3 笔账，再见！` → 文件里 3 行；第二轮重开，3 笔全在。

- 易错 ①：**不做空账本判断** → 用户可能先把文件删了、再开程序、直接退出 → `FileWriter` 一开就把文件清空，老账本没了（"用空数据覆盖好文件"是数据丢失的经典事故）
- 易错 ②：`saveBills` 写在 `break` 后面 → 永远执行不到，**数据没存**（而且不报错，最难查）
- 易错 ③：`break` 写在 `saveBills` 之前 → 保存没跑就退出了
- 加分理解：`saveBills` 用的是 `new FileWriter(FILE)` = **覆盖写**。如果想"追加"应该写 `new FileWriter(FILE, true)`（Day17 学过的追加开关）——但账本要的是覆盖（每次都把完整明细重写一遍），所以这里用覆盖是对的

---

## 两轮实测记录

**第一轮**（先删掉 `账本.txt`）：

```
菜单 3 → 还没有明细，先记一笔吧
菜单 2 → 还没有账，先记一笔吧
菜单 5 → 总收入：0 / 总支出：0 / 结余：0
记 3 笔 → 菜单 5 → 总收入：100 / 总支出：-20 / 结余：80
选 6 → 已保存 3 笔账，再见！
```

**文件内容**（记事本打开的样子，⇥ 是 Tab）：

```
吃饭⇥-12⇥食堂午饭
吃饭⇥-8⇥早饭
收入⇥100⇥兼职工资
```

**第二轮**（重新运行）：

```
菜单 3 → 1. 吃饭 -12 食堂午饭
         2. 吃饭 -8 早饭
         3. 收入 100 兼职工资
菜单 2 → 吃饭：-20 / 收入：100      ← 分类汇总按读回来的明细重算，正确
删除序号 1 → 已删除 → 明细剩 2 笔 → 菜单 2 变 吃饭：-8
选 6 → 已保存 2 笔账，再见！
```

## 面试可背

1. **为什么要持久化**：内存是"临时草稿纸"，程序一停就没了；文件/数据库才是"本子"。
2. **为什么用 Tab 而不是空格分隔**：字段里可能出现空格，用空格会错位；用 Tab（或 CSV 的引号转义）才安全。
3. **`FileWriter` 覆盖 vs 追加**：`new FileWriter(f)` 覆盖、`new FileWriter(f, true)` 追加。
4. **读外部数据的原则**：逐行读 + 逐行容错，一行脏数据不能毁掉整体。