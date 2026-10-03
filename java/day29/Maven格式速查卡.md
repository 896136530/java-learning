# Maven 格式速查卡（Day29 的"真正该背下来的东西"）

> 这张卡解决一个问题：**拿到一个 Java 项目，我知道它的"标准长相"是什么。**
> Day29 你在 Maven 里写了 5 道题，但格式是散的 —— 这里一次收拢。

---

## 一、标准目录结构（⚠️ 位置错了 Maven 就找不到）

```
项目根目录/                          ← 执行 mvn 命令的地方
├── pom.xml                          ← ⭐ 必须有，Maven 项目的身份证
├── src/
│   ├── main/                        ← main = 正式代码（会被打包）
│   │   ├── java/                    ← ⭐ 所有 .java 都放这里（可以有很多层包）
│   │   │   └── com/xiaoming/app/    ← 包名 = 文件夹路径（反向域名）
│   │   │       ├── Main.java
│   │   │       └── service/OrderService.java
│   │   └── resources/               ← ⭐ 配置文件、模板（注意：不是 java/ 里！）
│   │       ├── application.properties
│   │       └── db.properties
│   └── test/                        ← 测试代码（Day31 会用，现在可以不管）
│       └── java/
└── target/                          ← ⚠️ 别手动动它！mvn 生成的产物
    ├── classes/                     ← 编译产物（.class）+ 从 resources 复制的文件
    └── *.jar                        ← 打包产物
```

### 三条铁律（位置错了必出坑）

| 规则 | 放错的后果 |
|---|---|
| `.java` 必须放 `src/main/java/` 下 | 放外面 → **Maven 根本不编译它**（不报错，就是不生效）|
| **配置文件**必须放 `src/main/resources/` | 放到 `src/main/java/` 旁边 → 打包时**不进 jar**，运行时读不到 |
| **包名 = 文件夹路径** | `package com.a.b;` 的类必须在 `.../com/a/b/` 里 → 不一致就编译报错 |

> 📌 **`resources/` 为什么特殊**：`mvn compile` 会把 `resources/` 里的文件**原样复制**到 `target/classes/`，
> 打包时再进 jar。所以**"跟着代码走的文件"必须放这里**（Day29 Ex3 教的就是这个）。

---

## 二、pom.xml 完整骨架（背下这 6 块）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
  <modelVersion>4.0.0</modelVersion>

  <!-- ① GAV：我是谁（三行，全世界唯一标识一个项目）-->
  <groupId>com.xiaoming</groupId>          <!-- 组织/公司（反向域名）-->
  <artifactId>day29-maven</artifactId>     <!-- 项目名 -->
  <version>1.0</version>                   <!-- 版本号 -->

  <!-- ② 属性：全局变量（下面的插件/依赖可以引用）-->
  <properties>
    <java.version>21</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <!-- ③ 依赖：我要用什么别人的东西 -->
  <dependencies>
    <dependency>
      <groupId>com.mysql</groupId>          <!-- 谁写的 -->
      <artifactId>mysql-connector-j</artifactId>  <!-- 叫什么 -->
      <version>8.4.0</version>              <!-- 哪个版本 -->
      <scope>runtime</scope>                <!-- 可选：什么时候需要（见下）-->
    </dependency>
  </dependencies>

  <!-- ④ 构建：打包时用什么插件 -->
  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.5.1</version>
        <configuration>
          <transformers>
            <transformer implementation="...ManifestResourceTransformer">
              <mainClass>ex1.HelloMaven</mainClass>   <!-- ⭐ 决定 java -jar 跑谁 -->
            </transformer>
          </transformers>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

> 💡 **Day30 会多两块**：`<parent>`（继承版本管理，依赖就不用写 version 了）和 `spring-boot-maven-plugin`。
> 但上面这 6 块**结构完全一样** —— SpringBoot 也是 Maven 项目。

### 依赖的三个坐标 = 目录路径（找到 jar 的钥匙）

```
groupId:artifactId:version
   ↓        ↓        ↓
com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar
└───────────── .m2 仓库里的真实路径 ─────────────┘
```

**背下来这条**：GAV 三行倒过来就是 `.m2` 里的文件夹路径（Day29 Ex2 验证过）。

### `<scope>` 常用三个

| scope | 意思 | 例子 |
|---|---|---|
| （不写）`compile` | 编译+运行都要 | 你写代码要用的库 |
| `runtime` | 编译不用、**运行时**要 | **MySQL 驱动**（你代码里只调 JDBC 接口，没直接 new 驱动类）|
| `test` | 只有测试要 | JUnit |

> ⚠️ 驱动写 `runtime` 也能跑，写成默认 `compile` 也没错 —— 但**知道有这个区分**是进阶点。

---

## 三、Maven 的三个身份（一句话各自作用）

| 身份 | 干什么 | 你见过的证据 |
|---|---|---|
| **包管理器** | 按 pom 的 GAV 去 `.m2` / 中央仓库拿 jar | Ex2：驱动在 `.m2\repository\com\mysql\...` |
| **项目规范** | 规定目录长什么样 | 上面的目录结构 |
| **构建工具** | 编译 → 测试 → 打包一条流水线 | Ex4：瘦 jar / 胖 jar |

---

## 四、命令速查（常用的 8 个）

```bash
mvn compile                 # 只编译（⭐ 报错先跑这个，比 exec:java 清楚）
mvn clean                   # 删 target（产物是旧的时用）
mvn package                 # 打包（编+测+打成 jar）
mvn clean package           # ⭐ 最常用：先清再打（避免旧产物混进来）
mvn exec:java -Dexec.mainClass=ex1.HelloMaven    # 跑某个类（开发时快）
java -jar target/xxx.jar    # 跑打好的 jar（发货验收）
mvn dependency:tree         # 看依赖树（"这个类是哪个包带来的？"）
mvn dependency:build-classpath   # 看 Maven 帮你拼的完整 classpath
```

### 生命周期（记住这三步就够）

```
clean  →  compile  →  test  →  package  →  install
 删target   编译       跑测试    打jar      装进.m2
```

**关键**：`mvn package` 会**先自动执行** compile 和 test —— 所以你不用挨个敲。
但**失败时要知道它停在哪一步**（这就是为什么"先单独 `mvn compile`"更快定位问题）。

---

## 五、⭐ 核心技能：把普通 Java 项目"改造"成 Maven 项目

这是**面试/实际工作里真会用**的操作，你现在就能做：

```
改造前（Day21~28 的样子）：              改造后（Maven 的样子）：
java/day28/                             java/day28-maven/
├── Day28_Ex1.java                      ├── pom.xml               ← 新建
├── Db.java                             └── src/main/
├── 运行Day28.bat                              ├── java/day28/
└── ← 靠 bat 里的 -cp 找 jar                        ├── Day28_Ex1.java
                                                    ├── Db.java
                                                    └── ...
```

**五步**：

1. **建目录**：`src/main/java/包名/`、`src/main/resources/`
2. **挪文件**：`.java` 全挪进 `src/main/java/包名/`，**每个文件首行加 `package 包名;`**
3. **写 pom.xml**：GAV 三行 + 把 bat 里 `-cp` 的那个 jar 写成 `<dependency>`
4. **删 bat**：`mvn compile` / `mvn exec:java` 取代 `javac -cp` / `java -cp`
5. **验货**：`mvn clean package` → `java -jar`

> **第 2 步的 `package` 声明最容易被漏**：`javac` 手动编译时可以不写（默认无名包），
> 但 Maven 强制要求目录结构和包名一致 —— **不写 package 的类在 Maven 里能编译，但会很别扭**。
>
> 💡 **改造的价值**：改造完的项目"换台电脑 `mvn compile` 就能跑"，不用拷 lib、不用改路径。
>
> ✅ **这五步我已实测验证过**（2026-10-03）：临时建了 `demo` 包 + `src/main/resources/msg.txt` + 极简 pom，
> `mvn compile` exit=0、`mvn exec:java -Dexec.mainClass=demo.Hello` 跑出结果、
> **`msg.txt` 确实被自动复制到了 `target/classes/`** —— 说明"resources 跟着代码走"这条是真的。

---

## 六、报错对照表（见到就知道怎么回事）

| 报错 | 真正的意思 | 怎么办 |
|---|---|---|
| `'mvn' 不是内部或外部命令` | Maven 不在 PATH | 用 `C:\Users\89613\bin\mvn.bat`（或重启 VS Code）|
| `找不到符号: 类 SQLException` | **缺 import**（跟 Maven 无关）| 加 `import java.sql.SQLException;` |
| `找不到符号: 方法 getXxx()` | 方法没写 | 写方法，或先写空壳 `return 0;` |
| `程序包 xx 不存在` | **pom 里少依赖** | 加 `<dependency>` |
| `Could not find artifact ... in central` | **版本号写错了**（仓库里没这个版本）| 查正确版本号 |
| `An exception occurred ... exec:java` | ⭐ **编译就没过**（真凶被藏了）| **先跑 `mvn compile`** |
| `没有主清单属性` | jar 里缺 `Main-Class` | pom 里配 shade/boot 插件写 `<mainClass>` |
| `Unable to access jarfile` | **路径错**（不是 jar 内部问题）| 检查 `target\` 前缀 |

---

## 七、自检清单（写完一天 Maven 作业后对一遍）

- [ ] `src/main/java/` 下的每个 `.java` 首行**都有 `package xxx;`**，且和目录对得上
- [ ] 配置文件在 `src/main/resources/`，**不是** `src/main/java/` 里
- [ ] pom.xml 有 **GAV 三行** + 需要的 `<dependency>`
- [ ] `mvn compile` 干净通过（有错就先修编译错）
- [ ] `mvn clean package` **BUILD SUCCESS**
- [ ] `java -jar target\xxx.jar` 能跑（不是只在 IDE 里能跑）
- [ ] `target/` **没有提交到 git**（.gitignore 里有）
- [ ] 删掉的旧 bat / 手写 `-cp` 命令不再是唯一运行方式

---

## 八、你现在能回答这三个问题，就说明"格式"这块过关了

1. 一个 `.java` 文件放在 `src/main/java/com/a/B.java`，那它的首行必须写什么？
   <details><summary>答案</summary>`package com.a;` —— 包名 = 目录路径，对不上就报错。</details>

2. 我有 20 个项目都用 MySQL 驱动，难道要下载 20 次？
   <details><summary>答案</summary>不用。jar 只有一份，在 `.m2\repository`（全机器公用）；每个项目只在 pom 里**声明**要用它。</details>

3. 我想把驱动从 8.4.0 升到 9.0.0，要改几个地方？
   <details><summary>答案</summary>一处：pom.xml 里那个 `<version>`。`mvn clean package` 就换了（Day29 自测题验证过）。</details>