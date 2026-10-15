# Java 学习之路 📚

> 河南大学 · 软件工程 📖 | 目标：Java 后端方向 → 大创 🎯 → 实习/就业 💼
> 双线并行：**Java 就业主线（基本盘）** + **Python 蓝桥杯竞赛线（加分项）**

## 仓库结构（三线分开）

```
java-learning/
├── README.md          ← 三线导航（进度表 + 说明）
├── 任务生成规范.md      ← 🔑 换新对话必读：任务格式/批改规范/当前进度/**DSH 环境与待办**（看第 10 节）
├── java/              ← 🟦 Java 就业主线（day1~day32 + 学习路线 + PPT + 项目-记账本）
└── lanqiao/           ← 🟩 蓝桥杯 Python 竞赛线（备考路线 + python-day1 ~ python-day10）
```

## 📌 Java 线（`java/`）

| 内容 | 说明 |
|---|---|
| `学习路线.md` | Java 就业主线：完整时间线、Day 级计划、里程碑 |
| `day1~day32/` | 每天：知识点 → 练习题 → 标准答案（**day29 = Maven ✅、day30 = SpringBoot ✅已批改、day31 = 全局异常处理 ✅已批改、day32 = 参数校验深入 🔄已生成待练习**）|
| `项目-记账本/` | 📒 命令行记账本：Step1 分类汇总 ✅ 已验收 / Step2 明细+删除+防崩 ✅ 已验收（A~D 四处全过）/ Step3 持久化 🔄 已生成待做（`Step3_记账本.java` + `Step3任务书.md` + `Step3标准答案.md`）|
| `启动MySQL.bat` | 🔧 一键起 MySQL 8.4.5（Day29 之后常用；不需管理员，自动探活 + 等就绪）|
| `AI时代学Java-PPT/` | 网页 PPT 分享（Swiss Style）|

## 📌 蓝桥杯线（`lanqiao/`）

| 内容 | 说明 |
|---|---|
| `蓝桥杯备考路线.md` | Python 大学 B 组：按周备考计划（2026-09 → 2027-04）、教材对照表、报名提醒 |
| `python-day1/` | ✅ print/变量/input/类型转换/split——作业已批改上传（2 处格式微瑕，用户决定保留） |
| `python-day2/` | ✅ 作业已批改通过并上传（if 判断 + 循环）|
| `python-day3/` | ✅ 作业已批改通过并上传（字符串 + 列表）|
| `python-day4/` | ✅ 作业已批改通过并上传（字典 + 集合；Review 待补）|
| `python-day5/` | ✅ 作业已批改（**Ex1~Ex5 + Review 全绿**；Ex3/Ex4 按用户要求代改后复验通过，Review 为参考答案供复习）|
| `python-day6/` | ✅ 作业已批改（Ex1~Ex4 全绿；Ex5 数值对、输出格式已代改；Review 矩阵乘法用户留待复习自写）|
| `python-day7/` | ✅ 作业已批改并上传（**Ex1~Ex5 全绿**，14 组用例含边界；Ex5 少打第一行由 AI 按用户要求代改；Review 按用户要求留待复习自写；已清掉 Code Runner 的 `tempCodeRunnerFile.py`）|
| `python-day8/` | ✅ 作业已批改并上传（**Ex1~Ex5 全绿**，16 组用例：Ex1 gcd/lcm/isqrt 全对、Ex4 combinations 边界 n=1→0 正确、Ex5 bisect 四组含"插在相同元素前"全对；AI 代改 Ex2/Ex3 两处**输出格式**——Ex2 原打印 `[('b', 3)]`+整个 Counter、Ex3 原打印列表 `['b','c']`，已改为 `b 3`/`4` 和 `bc`，复验通过；Review 未写）|
| `python-day9/` | ✅ 作业已批改并上传（**Ex1~Ex6 全绿**，31 组用例含中文/空格/长度1/大小写/不重叠计数；Ex3 中文归类正确、Ex4 用 `join` 修好了 Day8 栽过的"输出列表"、Ex5 手写 find 版与 count 版结果完全一致；**唯一问题**：`Ex5_count_sub.py` 里 count 版和手写版**同时生效**，交替读了 4 行输入 → 按题目只给 2 行会 `EOFError`（题目原话是"把第 1 遍注释掉"，做了但没注释）；Review 未写；顺手清掉 `tempCodeRunnerFile.py`）|
| `python-day10/` | 🔄 **已生成待练习**（**前缀和与差分**：一维/二维前缀和、区间和 O(1)、差分区间加、覆盖次数、借教室；**6 题 + 复习 + 3 道蓝桥真题**；真题**带输入样例写在题目里**，不配测试数据文件、不给答案）|
| 逐日路线 | 见 `蓝桥杯备考路线.md` 的「📅 逐日路线」表——**Day 10 起每天配蓝桥风格真题，输入样例直接写在题目里**（不用自己上网找题）|

## 📌 数据结构（不再单独建线）

> 2026-09 起学校开了《数据结构》课，**老师每次课结束都留实验报告**——跟课 + 交实验就够了，本仓库不再维护 `datastructure/` 线（原跟课三件套已于 2026-09-20 删除，历史在 git 里可查）。
> 实验报告相关只做一件事：**代码卡住时喊我检查**（如 2026-09-20 的实验2 顺序表：修好 13 个编译错 + MergeList 越界读/去重两 bug + 加菜单16 随机填充测试数据）。

## 打卡进度

| Day | 主题 | 状态 |
|---|---|---|
| Day 1 | 变量与数据类型 | ✅ |
| Day 2 | 运算符与流程控制 | ✅ |
| Day 3 | 数组与 Scanner | ✅ |
| Day 4 | 方法（Method） | ✅ |
| Day 5 | 类与对象 | ✅ |
| Day 6 | 封装（private + getter/setter） | ✅ |
| Day 7 | 继承（extends / super / 重写） | ✅ |
| Day 8 | 多态（重写 / 重载 / 向上转型） | ✅ |
| Day 9 | 接口 + 抽象类（implements / abstract） | ✅ |
| Day 10 | Object 类 + 常用方法（toString / equals / hashCode） | ✅ |
| Day 11 | String 字符串（比较 / 常用方法 / 不可变） | ✅ |
| Day 12 | ArrayList（动态数组 / add / remove / set） | ✅ |
| Day 13 | HashMap（键值对 / put / get / keySet） | ✅ |
| Day 14 | 其他集合（HashSet / LinkedList / Queue） | ✅ |
| Day 15 | 异常处理（try / catch / finally） | ✅ |
| Day 16 | 泛型（Generic `<T>`：泛型类 / 泛型方法） | ✅ |
| Day 17 | IO 流（文件读写：FileReader / FileWriter） | ✅ |
| Day 18 | 多线程（Thread / Runnable / synchronized） | ✅ |
| Day 19 | 多线程综合实战（分工合并 / 银行取款 / 并发集合） | ✅ 五题全绿已上传 |
| Day 20 | 网络编程基础（Socket：ServerSocket / 客户端-服务器模型） | ✅ 五题全绿已上传 |
| Day 21 | JDBC 数据库访问（MySQL 8.4.5 本机已装：`root/root`，库 `day21`） | ✅ Ex1~5+Review 全绿已上传 |
| Day 22 | 集合框架全解（HashMap 原理 / TreeMap / Comparable 与 Comparator） | ✅ 6/6 全绿已上传 |
| Day 23 | 综合实战·学生成绩排行榜（类 + List + Map + 排序全家桶组装） | ✅ 作业已批改并**修完**：每题独立空目录编译运行 **6/6 逐字符一致**（AI 代改 4 处：Ex2 补 Student 类、Ex4 排序方向、Ex5 map 作用域、Review 越界防护）|
| Day 24 | 文件与异常实战（成绩单导入导出：字符流读写 / 追加 / 异常兜底）| ✅ 批改 **5/5 全绿**（Ex1~Ex5 与期望逐字符一致；Review 未写）|
| Day 25 | MySQL 基础（建库建表 / CRUD / 条件查询 / 分组聚合 / 多表 JOIN）| ✅ 批改 **Ex1~Ex5 全对**（Ex5 漏写的 ORDER BY + 别名已按用户要求代改并复验）；Review 未写 |
| Day 26 | JDBC 实战·成绩管理系统 v1（PreparedStatement / ResultSet / 聚合下推 / LIMIT 参数化）| ✅ 批改 **Ex1~Ex5 全绿**（与期望逐字符一致 + 21 项刁钻用例全过；Ex5 的 TODO 注释被粘贴搅乱已代整理）；Review 未写 |
| Day 27 | DAO 分层 + 事务（实体类 / mapRow / addBatch 批量 / setAutoCommit→commit→rollback）| ✅ 批改 **Ex1~Ex5 全绿**（MySQL 实测：Ex1 逐字符一致、Ex2 7→3→10 行、Ex3 修复 `rs.next()` 吃行后 4 人正确、Ex4 对账 5000 守恒、Ex5 五道防线 + 转账 200 成功且钱守恒；AI 代改机械错 5 处：Ex1 空格、Ex2 泛型空格、Ex4 `rs→rs1`+`throws`+删半成品块、Ex5 `throws`+表名 `Account`/`back_account`→`bank_account`）；Review 未写 |
| Day 28 | DAO 收口 + 事务隔离级别 + 连接池（影响行数 / 聚合下推 / REPEATABLE READ / 手写迷你池 + 动态代理）| ✅ 批改 **Ex1~Ex5 全绿**（MySQL 实测逐条对齐：Ex1 `7→8→7` + 改不存在的人返回 0、Ex2 `{一班=3, 二班=2, 三班=2}`、Ex3 隔离级别四场景全对（B 看不到未提交的 700）、Ex4 池 3→借空→第 4 个 null→还回复用、Ex5 新建 193ms vs 池化 60ms；**用户独立写对 Proxy 包壳并自己补了 import**）；AI 代改 1 处（Ex2 `return map;F` 多打的字母）；Review 未写；Ex2 平均分显示 `78.0`（main 里是 println 拼接，要 `printf("%.2f")` 才显示 `78.00`，机械细节未改）|
| Day 29 | **Maven 与项目结构**（pom.xml / GAV / 本地仓库 / 标准目录 / 生命周期 / 瘦 jar vs 胖 jar / 类路径读配置）| ✅ 批改 **Ex1~Ex5 全绿 + Ex4 观察记录 5 条**（真机实测：Ex1 驱动 8.4.0 + `SELECT 1 = 1 ✅`、Ex2 jar 2533399 字节、Ex3 读到 4 行、Ex4 瘦 16906 / 胖 4447423 字节且瘦 jar 报"没有主清单属性"、Ex5 5 本 577.5 元 + 录入后 6 本；`java -jar` 发货成功跑出小书店）；**新增 Review 题 `ReviewThen.java`**（AI 补的，待写）；AI 代改 2 处（`import java.sql.SQLException`、`jarSize()` 里 `repo` 作用域）+ 修正期望值 8 处（580.5→577.5）；Review 未写 |
| Day 30 | **SpringBoot 上手**（start.spring.io 同款结构 / starter + parent / 内嵌 Tomcat / REST 接口 / JPA 一行 DAO / H2 内嵌库 / 可执行 jar 48 MB）| ✅ 批改 **6/6 全绿（含附加题）**（真机实测：TODO① `findByDayNoAndDone`+`/tasks/todo?day=5` 返回 1 条、TODO② 完成率 `62.5`、TODO③ 观察到 `/tasks/999` = 200+0 字节、TODO④ `save` 返回 `{"id":9}` 且条数 8→9、TODO⑤ `@Value` 读配置正确、**TODO⑥ 附加题 stream+`Task::getTitle`+`Collectors.joining("")`+`StringUtils.length` 得 `totalChars=91` 完全正确**；`mvn clean package` → jar **48,188,284 字节**（比加依赖前大 640 KB）；观察记录 4 块补齐）<br>⭐ **意外挖到的真坑**：`.properties` 直接写中文 → 接口返回乱码 `æçå¦ä¹ å©æ`（`java.util.Properties` 默认按 ISO-8859-1 解码，`-Dspring.config.file-encoding=UTF-8` 也救不回来）→ 改用 `\u6211\u7684\u5b66\u4e60\u52a9\u624b` 转义，实测字节正确；已写进 `知识点.md` 6.5 节<br>AI 代改机械/格式 3 处（Repository 答案行重复 + 误导注释、`@GetMapping("todo")`→`"/todo"` 并挪位置、TODO⑥ 缩进+删调试 println）|
| Day 31 | **全局异常处理 + 统一响应**（`@RestControllerAdvice` / 自定义异常体系 / `Result<T>` 统一信封 / HTTP 状态码 vs 业务码 / 400-404-409-500 辨析 / `@Valid` + fieldErrors / 日志级别 warn vs error）| ✅ **批改通过（2026-10-14，8 步全过 / 真机 100% 通过）**（1 个 SKU 库存项目 + 8 步（3 观察 + 2 代码 TODO + 1 思考 + 验收）+ 知识点 13 节 + 标准答案；**13 个场景全部真机实测**：启动约 **4 秒**、`mvn clean package` BUILD SUCCESS；实测状态码 `GET /sku/999`=**404**、`/abc`=404（自带格式）、`/sku/boom`=**500（自带格式且泄露类名 `com.xiaoming.day31.sku.Sku.getTitle()`）**、`outbound?count=0`=**400**、`outbound?id=2&count=100`=**409**、`POST /sku` 空书名负价格=**400**+fieldErrors 三字段、`DELETE /sku/999`=404；**TODO①②的答案已在临时副本中打补丁验证**（兜底→`500 服务器开小差了`；delete 加库存判断→`/sku/1`=**409 「深入理解 Java 虚拟机」还有 10 件库存，不能删除**、`/sku/3`=200）；交付版**故意留两处错**：`/sku/boom` 走自带错误页 + `DELETE /sku/1`/`/sku/2` 返回 200（TODO 未写）<br>✅ **批改结论（2026-10-14）**：**8 步全过** —— TODO① 兜底由用户自写，并在实测中**自己发现**了 `/abc` 从 404 变 500 是兜底误伤了 `NoResourceFoundException`（Spring 6 里它是 `Exception` 的子类），自己加了专属 handler 修回 404（这是"兜底越宽越容易误伤"的活教材）；TODO② 初版误用 `repository.deleteById`（把 Day30 的 JPA 记忆带进了内存 Map 项目）编译报错，改对后 409/404/200 全对。详见 `任务生成规范.md` 第 8 节的 Day31 条目<br>⭐ 设计取舍：今天**刻意不用数据库**（内存 Map + 3 条初始数据），隔离无关变量专心学异常；日志实测 WARN（业务异常，只 1 行）vs ERROR（系统异常，完整堆栈）|
| Day 32 | **参数校验深入**（分组校验 / 自定义校验注解：`@Valid` vs `@Validated`、标记接口 `OnCreate`/`OnUpdate`、**静默失效**、`@Constraint` 注解三件套、`ConstraintValidator` 的 `initialize`+`isValid`、`{max}` 插值、**两条校验路径两个异常**）| ❌ **批改：未通过（2026-10-15，约 43~48/100）**<br>⭐⭐ **唯一根因 = 组名静默失效**：`SkuForm` 里自建了一套同名组接口 `SkuForm$OnCreate`，与 Controller 引用的 `valid.OnCreate` **不是同一个 Class** → 请求体注解层**一条都没执行**（实测：`POST {}` 的 fieldErrors=**null（不是 4 条）**、`POST {"id":99}`=**200 越权通过**、`code:"abc"`=**200 脏数据入库**、`PUT {price:99}`=**404「SKU 不存在：id=null」**、`stock:800`=**200**）<br>✅ **代码写法本身全对**：第 7 步 `ConstraintViolationException` **5/5 全绿**（limit key 截断成功 + WARN 无堆栈）、第 5/6 步两个自定义校验器**框架级真机 100% 正确**（SkuCode 整串 matches + 放空值；MaxStock 真读到注解的 500）、**Day31 老四场景未退化**<br>AI 代改机械遗漏 2 处（`stock` 上被删的 `@MaxStock(max=500)` 整行、`code` 两注解缺 `groups`）→ 改后 31 个请求重打**逐字节一致**（证明不是接口失效原因）<br>**待修：把组名统一成 `valid.OnCreate`/`valid.OnUpdate` 后重验**（1 个 SKU 库存项目 + **8 步 / 100 分**（1 步观察 + 1 步思考 + 4 个代码 TODO + 1 步接异常 + 3 道思考题）+ 知识点 15 节 + 标准答案；**19 个场景全部真机实测**：启动 **1.838 秒**、`mvn -B clean package` BUILD SUCCESS。答案版实测：`POST /sku` 空对象=**400 + fieldErrors 正好 4 条 `{price,title,code,stock}`（没有 id）**、带 `id:99`=400、`code:"abc"`=**400「商品编码格式不对（正确格式：SKU-0001）」**、`stock:800`=**400「库存不能超过 500」**、`stock:500`=200（边界）、`PUT {id:1,price:99}`=**200 且 title 未变（部分更新）**、`PUT` 不带 id=400、`PUT {"id":1,"title":""}`=400、`PUT {"id":999}`=404、`search?limit=0`=**400「limit 至少是 1」（改之前是 500）**、`limit=99`=400、`limit=50`=200、`code` 重复=**409**、Day31 老四样（404/404/409/500）全未退化<br>⭐⭐ **交付版故意留的 5 处可观察状态**：① `id` 上 `@Null`+`@NotNull` 同在 `Default` 组 → **新增怎么都成功不了**（"为什么必须分组"的铁证）② `PUT` 任意请求体都 400（一套注解表达不了部分更新）③ `SkuCodeValidator` 占位 `return true` → `code:"abc"` 能过 ④ `MaxStockValidator` 把上限**写死 1000**（注解上写的是 500）→ `stock:800` 能过 ⑤ `search?limit=0`=**500**（`ConstraintViolationException` 没人接，掉进兜底）<br>⭐⭐ **实测挖出的"静默失效"活教材（今天最值钱的一条）**：只改 Controller 的 `@Validated(OnCreate)` 而忘给注解加 `groups` → `Default` 组不再参与 → **带 `id:99` 的新增返回 200（越权通过）**、`POST {}` 返回 400 但 **fieldErrors=null**（其实是 Service 手写校验兜住的）、`PUT {price:99}` → **404「SKU 不存在：id=null」**（错得驴唇不对马嘴）<br>⭐ 设计取舍：延续 Day31 用内存 `LinkedHashMap`（无数据库）隔离无关变量；`@MaxStock(max=500)` **特意与占位里的 1000 不同**，让"忘读注解参数"这件事能被观察出来 |

## 目标

- 🏆 蓝桥杯（Python 组，**2027-04 省赛**；报名窗口 2026-10~12）—— 详见 `lanqiao/`
- 🎯 大创项目（SpringBoot 小系统申报）
- 💼 大三/大四实习 → Java 后端就业