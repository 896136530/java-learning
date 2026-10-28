@echo off
chcp 65001>nul
setlocal enabledelayedexpansion
rem ================================================================
rem  Day35 一键脚本：H2 -> 真 MySQL 迁移 + 还 Day32 的"数据库约束"债
rem  ⚠️ 本脚本默认端口 8081（8080 可能被 day33 的 jar 占着）
rem ================================================================
rem ---- 找 Maven：优先 bin\mvn.bat 快捷方式，找不到就用 VS Code 扩展自带的那份 ----
set MVN=C:\Users\89613\bin\mvn.bat
if not exist "%MVN%" set MVN=C:\Users\89613\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd
set HERE=%~dp0
set PICK=%~1
set PORT=8081
set JAR=target\day35-springboot-1.0.jar
set TMPF=%TEMP%\day35_probe.txt
rem ⚠️ 这个路径是按【本机】写的；换机器请改成本机 MySQL 的 bin\mysql.exe，
rem    或者把 bin 加进 PATH 后改成 set MYSQL=mysql
set MYSQL=E:\mysql\bin\mysql.exe
set DBNAME=day35

if not exist "%MVN%" (
    echo [ERROR] 找不到 Maven：
    echo         %MVN%
    echo         装/更新 "Oracle Java" 扩展，或建一个 C:\Users\89613\bin\mvn.bat 快捷方式。
    pause & exit /b
)

:menu
if not "%PICK%"=="" goto run
echo.
echo ============ Day35 换真 MySQL + 数据库约束债 任务菜单 ============
echo    1. 启动应用（自动打包＋运行，Ctrl+C 停止）      ^<- 先跑这个（H2 状态）
echo    2. 只打包（生成可执行 jar，不运行）
echo    3. 打包并运行
echo    4. ★★ 一键取证：还债演示（重复插入 500 -^> 409 前后对比）  ^<- 今天最重要
echo    5. 建库 / 看库（MySQL 环境自检：版本＋字符集＋有哪些库）
echo    6. mvn dependency:tree（看 h2 有没有删掉、mysql 驱动是哪个版本）
echo    7. ★ 并发打满连接池（4 个并发 /db/hold，看"连接不可用"原话）
echo    8. 只复位数据库（POST /db/reset）
echo    9. 清理实验残留（t_money / t_time 等临时表）
echo   10. 便捷提示（要访问哪些地址 / 今天的取证四步法）
echo   11. mvn clean
echo ==================================================================
set /p PICK=输入编号后回车：
if "%PICK%"=="" goto menu

:run
cd /d "%HERE%"
for /r "%HERE%src" %%f in (tempCodeRunnerFile.*) do (
    echo [clean] 删掉垃圾文件 %%f
    del /q "%%f" 2>nul
)
if "%PICK%"=="1"  goto start
if "%PICK%"=="2"  goto package
if "%PICK%"=="3"  goto package_run
if "%PICK%"=="4"  goto probe
if "%PICK%"=="5"  goto mysqlenv
if "%PICK%"=="6"  goto tree
if "%PICK%"=="7"  goto pool
if "%PICK%"=="8"  goto reset
if "%PICK%"=="9"  goto cleanup
if "%PICK%"=="10" goto urls
if "%PICK%"=="11" goto clean
echo [INVALID] 没有这个编号
pause & exit /b

:start
rem ---- 为什么不用 mvn spring-boot:run？ ----
rem   spring-boot:run 会把应用"分叉"成另一个 Java 进程，classpath 变成命令行参数；
rem   而本机项目路径 C:\...\新建文件夹 (2)\... 里有【空格】，参数被空格截断 →
rem   子进程看不到 classpath → ClassNotFoundException。
rem   java -jar 不受影响（classpath 写在 jar 清单里），所以这里"先打包再 java -jar"。
call "%MVN%" -B package
if errorlevel 1 (
    echo.
    echo [FAIL] 打包失败，看上面 ERROR 那几行。
    echo        常见原因：编译错 / 依赖没下载完（重跑一次就好）
    echo        ⚠️ 第 3 步做完之后，这里报"找不到 org.h2.Driver"是【正常的】——
    echo           那是因为你删了 H2 依赖、但第 4 步的 url 还没换成 MySQL。
    pause & exit /b
)
call :checkport
echo.
echo ============ java -jar 启动中（Ctrl+C 停止）============
echo  启动后浏览器打开：
echo    http://localhost:%PORT%/public/ping     免登录探活，最先试这个
echo    http://localhost:%PORT%/db/pool         ★ 看 driverClassName 是 h2 还是 mysql
echo    http://localhost:%PORT%/db/state        一眼看全：orders / auditLog / 三行库存
echo    http://localhost:%PORT%/h2-console/     网页版 SQL 窗口（换成 MySQL 后就没用了）
echo  换 MySQL 之后要再看：
echo    http://localhost:%PORT%/db/columns      列类型 / 非空 / 默认值 / 字符集
echo    http://localhost:%PORT%/db/indexes      索引与列顺序（联合唯一索引靠它看）
echo    http://localhost:%PORT%/db/keys         约束清单（PK / UNIQUE / FK / CHECK）
echo ========================================================
call java -jar %JAR% --server.port=%PORT%
echo.
echo 服务已停止。
pause
exit /b

:package
echo.
echo ============ mvn clean package ============
call "%MVN%" -B clean package
if errorlevel 1 (
    echo.
    echo [FAIL] 打包失败。看上面 ERROR 那几行。
    pause & exit /b
)
echo.
echo ---- target 里的 jar ----
dir /b target\*.jar
echo ------------------------
echo 运行方式：java -jar %JAR% --server.port=%PORT%
pause
exit /b

:package_run
call "%MVN%" -B clean package
if errorlevel 1 (echo [FAIL] 打包失败 & pause & exit /b)
call :checkport
echo.
echo ============ java -jar 启动（Ctrl+C 停止）============
call java -jar %JAR% --server.port=%PORT%
pause
exit /b

rem ================= ★★ 一键取证：还债演示 =================
:probe
curl.exe -s -o nul "http://localhost:%PORT%/public/ping" >nul 2>&1
if errorlevel 1 (
    echo.
    echo [FAIL] 服务好像没在跑（连不上 http://localhost:%PORT%/public/ping）。
    echo        先回菜单选 1 启动应用，再回来选 4。
    pause & exit /b
)
echo.
echo ============ 还债演示（前提：服务已经在跑）============
echo  这个演示回答一个问题：**"数据库唯一索引拦下重复写入时，接口返回几？"**
echo.
echo  交付版（H2、还没动手）：  ->  500  「服务器开小差了」
echo  你改完 TODO④⑤ 之后（MySQL）：->  409 「数据已存在，不能重复写入」
echo.
echo  ⭐ 两个版本【数据库里都是干净的】（重复那行没插进去）——
echo     变的只是"怎么告诉用户"。别把"数据正确"和"用户体验"混成一件事。
echo ========================================================
call :debt "SKU-0001"
echo.
echo  ==== 对照：业务接口 POST /sku（交付版这里被 Java 的"先查再插"拦住）====
echo  ⚠️ /sku **不在** AuthInterceptor 的白名单里（白名单见 WebMvcConfig），
echo     所以【不带 token 一定是 401「未登录或登录已过期」】——
echo     这就是为什么要先 POST /login 拿 token，再带着 X-Token 打 /sku。
curl.exe -s -X POST "http://localhost:%PORT%/db/reset" >nul 2>&1
echo.
echo   1) POST /login 取 token：
curl.exe -s -X POST -H "Content-Type: application/json" -d "{\"username\":\"xiaoming\",\"password\":\"123456\"}" "http://localhost:%PORT%/login"
echo.
echo   2) 带 X-Token 打 POST /sku（重复编码）：
curl.exe -s -X POST -H "Content-Type: application/json" -H "X-Token: tk-xiaoming" -d "{\"code\":\"SKU-0001\",\"title\":\"重复编码实验\",\"price\":1,\"stock\":1}" -o "%TMPF%" -w "  POST /sku 重复编码 -> HTTP %%{http_code}" "http://localhost:%PORT%/sku"
echo.
type "%TMPF%"
echo.
echo  ⭐ 如果这里是 409 且消息是「商品编码已存在：SKU-0001」，
echo     说明它是【Service 里手写的那段】拦的，不是数据库 —— TODO⑤ 还没做。
echo  ⭐ 如果这里是 401「未登录或登录已过期」，那是 token 没带上 ——
echo     这正好证明 AuthInterceptor 在生效（但本菜单要看的不是它）。
echo.
echo ============ 演示结束 ============
echo  结论怎么读：
echo   1) 上面 "POST /db/exec" 的 HTTP 码，交付版 500 / 你改完 409；
echo   2) 启动窗口的日志里，你改完会有一行 WARN「唯一约束冲突…Duplicate entry …」
echo      交付版是一整坨 ERROR 堆栈（而且堆栈里也有 Duplicate entry，只是没人接住）；
echo   3) 两次查库的行数【都是 3】—— 唯一索引的功劳，跟 handler 无关。
pause
exit /b

:debt
curl.exe -s -X POST "http://localhost:%PORT%/db/reset" >nul 2>&1
echo.
echo --- 直接往库里插一个重复编码 %~1（绕过 Service，走 /db/exec）---
curl.exe -s -G --data-urlencode "q=INSERT INTO sku (code,title,price,stock) VALUES ('%~1','直接插库重复编码',1.00,1)" -o "%TMPF%" -w "  HTTP %%{http_code}  响应体 %%{size_download} 字节" "http://localhost:%PORT%/db/exec"
echo.
echo   ^| 响应体：
type "%TMPF%"
echo.
echo   ^| 查库（★行数应该还是 3，说明数据库拦住了）：
curl.exe -s -G --data-urlencode "q=SELECT COUNT(*) AS N FROM sku" "http://localhost:%PORT%/db/sql"
echo.
echo   ^| 顺带看约束清单里 sku 有哪几条：
curl.exe -s "http://localhost:%PORT%/db/keys"
echo.
exit /b

rem ================= MySQL 环境自检 =================
:mysqlenv
if not exist "%MYSQL%" (
    echo.
    echo [WARN] 没找到 mysql 命令行客户端：%MYSQL%
    echo        改一下本脚本里的 MYSQL 变量，或者用你的可视化工具（Navicat/DBeaver）。
    pause & exit /b
)
echo.
echo ============ MySQL 环境自检 ============
echo  ⚠️ 下面只做【只读】查询和"建自己的库"，绝不 DROP 任何东西。
echo  ⚠️ 你机器上的 day21/day25/.../day29 里有真数据，今天一条 SQL 都不许碰它们。
echo.
echo --- 1) 版本 ---
"%MYSQL%" -u root -proot -e "SELECT VERSION() AS mysql_version" 2>nul
echo --- 2) day35 库的字符集（必须是 utf8mb4）---
"%MYSQL%" -u root -proot -e "SHOW CREATE DATABASE IF NOT EXISTS %DBNAME%" 2>nul
if errorlevel 1 (
    echo.
    echo  day35 还不存在，现在建一个（utf8mb4）：
    "%MYSQL%" -u root -proot -e "CREATE DATABASE IF NOT EXISTS %DBNAME% DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci" 2>nul
    "%MYSQL%" -u root -proot -e "SHOW CREATE DATABASE %DBNAME%" 2>nul
)
echo.
echo --- 3) 本机有哪些库（★记下"别人的库"，全程别碰）---
"%MYSQL%" -u root -proot -e "SHOW DATABASES" 2>nul
echo.
echo --- 4) day35 库里有哪些表（换 MySQL 之前应该是空的）---
"%MYSQL%" -u root -proot -e "SHOW TABLES FROM %DBNAME%" 2>nul
echo.
echo --- 5) 时区表是不是空的（能解释时区那串乱码报错）---
"%MYSQL%" -u root -proot -e "SELECT COUNT(*) AS time_zone_name_rows FROM mysql.time_zone_name" 2>nul
echo.
echo   提示：想建专用用户（不用 root）就手敲这 3 句：
echo     CREATE USER IF NOT EXISTS 'day35'@'localhost' IDENTIFIED BY 'day35';
echo     GRANT ALL PRIVILEGES ON %DBNAME%.* TO 'day35'@'localhost';
echo     FLUSH PRIVILEGES;
pause
exit /b

:tree
echo.
echo ============ mvn dependency:tree ============
echo  要看的： ① 有没有 com.h2database:h2（第 3 步之后应该没有）
echo           ② com.mysql:mysql-connector-j 是哪个版本（不写 version 时 = 8.3.0）
call "%MVN%" -B dependency:tree
pause
exit /b

rem ================= ★ 并发打满连接池 =================
:pool
curl.exe -s -o nul "http://localhost:%PORT%/public/ping" >nul 2>&1
if errorlevel 1 (
    echo [FAIL] 服务没在跑。先选 1 启动。
    pause & exit /b
)
echo.
echo ============ 连接池实验（HikariCP）============
echo.
echo  当前池子状态（看 maximumPoolSize / minimumIdle / totalConnections）：
curl.exe -s "http://localhost:%PORT%/db/pool"
echo.
echo.
echo  现在并发打 4 个 /db/hold?ms=5000（每个占住一条连接 5 秒）。
echo  ⚠️ 默认池子 10 条 → 4 个都会 200。
echo  ⚠️ 想看到"连接不可用"，先改 application.properties 加三行再重启：
echo        spring.datasource.hikari.maximum-pool-size=2
echo        spring.datasource.hikari.minimum-idle=2
echo        spring.datasource.hikari.connection-timeout=2000
echo     然后回来选 7，就应该是 2 个 200 + 2 个 500。
echo.
echo  ★ 做实验必须把这三行删掉/注释掉再重启，不然后面会莫名其妙超时。
echo ------------------------------------------------------------
rem ⚠️⚠️ 下面这行踩过两个坑（真机复验 2026-10-23）：
rem   ① `^|` 不能写在**双引号里面** —— cmd 在双引号内不把 ^ 当转义符，
rem      于是 PowerShell 收到字面的 "^|" → "Unexpected token '^'" → 一条并发结果都出不来。
rem      → 用**裸 `|`** 就对了（双引号里的 | 不会被 cmd 当管道）。
rem   ② PowerShell 里的单引号**不要写成双份** '' —— cmd 不会把 '' 还原成 '，
rem      PowerShell 收到字面的 '' 直接语法错。→ 内层一个单引号都不写，
rem      空格/字母用 [char]32 / [char]120 拼。
for /f "delims=" %%i in ('powershell -NoProfile -Command "(1..4 | ForEach-Object { Start-Job -ScriptBlock { try { (Invoke-WebRequest http://127.0.0.1:%PORT%/db/hold?ms=5000 -UseBasicParsing -TimeoutSec 30).StatusCode } catch { [int]$_.Exception.Response.StatusCode } } } | Wait-Job | Receive-Job | Sort-Object | Group-Object | ForEach-Object { $_.Name + [char]32 + [char]120 + $_.Count })"') do echo   结果： %%i
echo ------------------------------------------------------------
echo.
echo  然后去【启动窗口】翻日志，抄下这句话（作业必填）：
echo    java.sql.SQLTransientConnectionException: HikariPool-1 - Connection is not available,
echo    request timed out after 2000ms (total=2, active=2, idle=0, waiting=0)
echo.
echo  ⚠️ 上面只是【样例】，别追求一字不差 —— 真机复验过，三处每次都不同：
echo    · 池名：没设 pool-name 时是 HikariPool-1；项目里设了 pool-name: HikariPool-day35，
echo            所以那句开头是 HikariPool-day35。池名由 pool-name 决定，不是固定的。
echo    · 毫秒：设 2000ms，实测会多出调度开销 → 见过 2009ms / 2013ms。
echo    · waiting：可能是 0 也可能是 1（它数的是"此刻还在排队"的请求数，
echo            而"超时那一刻"和"日志落盘那一刻"不是同一瞬间）。
echo  怎么读： total=2 一共两条；active=2 都被借走；idle=0 一条空闲都没有；
echo          waiting=0/1 都说明这条请求已经超时退出了，没在排队等。
pause
exit /b

:reset
echo.
curl.exe -s -X POST "http://localhost:%PORT%/db/reset"
echo.
echo   （复位后： orders=0、audit_log=1（那条 DAILY_CLOSE 靶子）、sku 三行种子）
pause
exit /b

rem ================= 清理实验残留 =================
:cleanup
if not exist "%MYSQL%" (
    echo [WARN] 没找到 %MYSQL%，改用 /db/exec 清理：
    curl.exe -s -G --data-urlencode "q=DROP TABLE IF EXISTS t_money" "http://localhost:%PORT%/db/exec"
    curl.exe -s -G --data-urlencode "q=DROP TABLE IF EXISTS t_time"  "http://localhost:%PORT%/db/exec"
    pause & exit /b
)
echo.
echo ============ 清理第 7 步做的临时表 ============
echo  ⚠️ 只删 day35 库里【我自己建的实验表】，业务表（sku/orders/audit_log/ch_demo）不动。
"%MYSQL%" -u root -proot %DBNAME% -e "DROP TABLE IF EXISTS t_money; DROP TABLE IF EXISTS t_time;" 2>nul
echo  剩下的表（应该只有业务表了）：
"%MYSQL%" -u root -proot -e "SHOW TABLES FROM %DBNAME%" 2>nul
pause
exit /b

:urls
echo.
echo ============ 要访问的地址（先选 1 启动服务）============
echo   --- 免登录 / 只读取证 ---
echo   GET  /public/ping        探活
echo   GET  /db/pool            ★ 连接池：driverClassName / 池子大小 / 空闲数 / connectionId
echo   GET  /db/state           一眼看全：orders / auditLog / 三行库存
echo   GET  /db/columns         列：类型 / 是否 null / 默认值 / 字符集
echo   GET  /db/indexes         索引：哪几列、列顺序（联合唯一索引两行）
echo   GET  /db/keys            约束：PRIMARY KEY / UNIQUE / FOREIGN KEY / CHECK
echo   GET  /db/orders          订单表全部内容
echo   GET  /db/audit           审计表全部内容
echo   GET  /db/sku             商品表全部内容（★含 is_deleted，逻辑删除的证据）
echo   GET  /db/charset-demo    ★★ utf8 vs utf8mb4 现场实验（emoji，只有 MySQL 能跑）
echo   POST /db/reset           复位（MySQL 里是 TRUNCATE，自增回到 1）
echo.
echo   --- 自由查库（只能 SELECT）---
echo   GET  /db/sql?q=SELECT COUNT(*) AS N FROM sku
echo   --- 自由写库（INSERT / UPDATE / DELETE；★★ 还债演示就靠它绕过 Service）---
echo   GET  /db/exec?q=INSERT INTO sku (code,title,price,stock) VALUES ('SKU-0001','重复',1.00,1)
echo.
echo   --- ⭐⭐ 今天的取证四步法（换库前后各做一遍，对比着看）---
echo   1) 启动，看日志里的 driverClassName（h2 -> mysql）与 jdbcUrl
echo   2) GET /db/pool      确认 driverClassName=com.mysql.cj.jdbc.Driver
echo   3) GET /db/keys + GET /db/indexes + GET /db/columns   确认约束/索引/类型/字符集
echo   4) 选菜单 4（还债演示）  看 500 -> 409 的变化，以及数据库里的行数没变
echo.
echo   --- ⭐ token 自查（AuthInterceptor 的规矩：先全拦，再按白名单放行）---
echo   WebMvcConfig 的白名单：/login  /public/**  /trace/**  /db/**  /order/**  /h2-console/**  /favicon.ico
echo    → /db/**   本脚本菜单 4/5/8/9 用的全是它，【不需要】token（带不带都一样）；
echo    → /sku/**  【需要】token（X-Token: tk-xiaoming 或 Authorization）；
echo      本脚本里只有菜单 4 的 POST /sku 打到 /sku —— 已经先 POST /login 再带上 X-Token 了。
echo    其它菜单没有任何请求打到受保护路径（/public/ping 也在白名单里）。
echo.
echo   --- 老功能（确认没退化；/sku/** 要带 X-Token 或 Authorization）---
echo   POST /login  {"username":"xiaoming","password":"123456"}  ^-^> token = tk-xiaoming
echo   GET  /sku（无 token）401 /（带 token）200 三条
echo   GET  /sku/search?limit=0     400 参数校验
echo   GET  /sku/999                404 不存在
echo   GET  /sku/boom               500 系统异常（统一 JSON）
echo   POST /sku/outbound?id=2^&count=100   409 库存不足
echo.
echo   提醒：PowerShell 里 POST 中文 body 要用 UTF8 字节数组，否则乱码。
echo   提醒：curl 千万别忘了 -X POST！漏了会发 GET，请求根本进不去方法，
echo         数据库"看起来很干净"——那是假象（本脚本制作时就踩过一次）。
echo   提醒：/db/exec 和 /db/sql 的 q 参数里有空格和引号，
echo         所以要用 --data-urlencode（脚本里已经这么写了，手敲记得带 -G）。
echo ==========================================
pause
exit /b

:clean
call "%MVN%" -B clean
echo [OK] target 已清理
pause
exit /b

rem ================= 检查端口有没有被占 =================
:checkport
netstat -ano | findstr ":%PORT% " | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    echo.
    echo [WARN] 端口 %PORT% 已经被占用了！可能是上次的服务没关干净。
    echo        占用的进程：
    netstat -ano ^| findstr ":%PORT% " ^| findstr "LISTENING"
    echo.
    echo        解决办法（按顺序试）：
    echo          ① 找到上面那个 PID，只关你自己启动的那个 java.exe：
    echo               taskkill /PID 进程号 /F
    echo             ⚠️ 别按名字批量杀 java.exe —— 可能杀掉 VS Code 的 Java 语言服务器。
    echo          ② 或者换个端口启动： 运行Day35.bat  之后不要用菜单 1，
    echo             而是手敲  java -jar %JAR% --server.port=8082
    echo.
    echo        提醒：8080 常年被 day33 的 jar 占着，本脚本默认用 8081 就是为了躲开它。
    echo.
    pause
)
exit /b