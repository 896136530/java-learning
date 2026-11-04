@echo off
chcp 65001>nul
setlocal enabledelayedexpansion
rem ================================================================
rem  Day37 一键脚本：MySQL 索引（二）
rem  1=建库造数  2=编译  3=计时  4=EXPLAIN  5=EXPLAIN ANALYZE
rem  6=mysql 命令行  7=看索引和索引大小
rem ================================================================
rem 切到脚本所在目录：以后全用相对路径（路径里有中文和括号，绝对路径容易踩 cmd 的坑）
pushd "%~dp0"
set MVN=C:\Users\89613\bin\mvn.bat
if not exist "%MVN%" set MVN=C:\Users\89613\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd
set PICK=%~1
rem 按本机写的；换机器改成你自己 mysql.exe 的路径
set MYSQL=E:\mysql\bin\mysql.exe
set JAR=target\day37-bench.jar
set JAVAOPT=-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8

if not exist "%MYSQL%" (
    echo [ERROR] 找不到 mysql.exe：%MYSQL%
    echo         改这个 .bat 里的 MYSQL 变量即可。
    pause & exit /b
)

:menu
if not "%PICK%"=="" goto run
echo.
echo ============ Day37  MySQL 索引（二） ============
echo   1. 建库 + 造 20 万行（会 DROP 重建 member 表）
echo   2. 编译打包（mvn package）
echo   3. 计时（选 SQL 文件）
echo   4. EXPLAIN（选 SQL 文件，看执行计划）
echo   5. EXPLAIN ANALYZE（选 SQL 文件，看真实行数/耗时）
echo   6. 进 mysql 命令行（day37 / day37）
echo   7. 看 member 上的索引 + 索引总大小
echo   0. 退出
echo =================================================
echo   文件编号：2=覆盖索引 3=EXPLAIN阶梯 4=排序分页
echo             5=索引下推 6=索引设计 9=我的实验
echo =================================================
set "PICK="
set /p PICK=请选择：
echo.

:run
if "%PICK%"=="1" goto setup
if "%PICK%"=="2" goto build
if "%PICK%"=="3" goto bench
if "%PICK%"=="4" goto explain
if "%PICK%"=="5" goto analyze
if "%PICK%"=="6" goto shell
if "%PICK%"=="7" goto showindex
if "%PICK%"=="0" goto quit
echo [ERROR] 没有这个选项：%PICK%
goto menu

:pickfile
set "F="
set "NAME="
set /p F=文件编号 [回车=9 我的实验]：
if "%F%"=="2" set "NAME=02_覆盖索引.sql"
if "%F%"=="3" set "NAME=03_EXPLAIN阶梯.sql"
if "%F%"=="4" set "NAME=04_排序分页.sql"
if "%F%"=="5" set "NAME=05_索引下推.sql"
if "%F%"=="6" set "NAME=06_索引设计.sql"
if not defined NAME set "NAME=90_我的实验.sql"
if exist "%F%" set "NAME=%F%"
set "FILE=sql\%NAME%"
if not exist "%FILE%" goto pickbad
goto :eof

:pickbad
echo [ERROR] 找不到文件：%FILE%
set "FILE="
goto :eof

:setup
echo [1/2] 建库 day37 + 账号 day37 + 造 20 万行（约 15~25 秒）...
"%MYSQL%" -u root -proot --default-character-set=utf8mb4 < sql\01_setup.sql
if errorlevel 1 goto failed
echo.
echo [2/2] 完成。继续请选 2 编译。
goto done

:build
if not exist "%MVN%" (
    echo [ERROR] 找不到 Maven：%MVN%
    pause & exit /b
)
echo 编译打包中（第一次可能要几十秒）...
call "%MVN%" -q package -DskipTests -f pom.xml
if errorlevel 1 goto failed
echo.
echo 完成，产物：target\day37-bench.jar
goto done

:bench
if not exist "%JAR%" (
    echo [ERROR] 还没编译，先跑菜单 2。
    goto done
)
call :pickfile
if not defined FILE goto done
set "N=20"
set /p N=每条跑几次 [回车 20]：
java %JAVAOPT% -jar "%JAR%" "%FILE%" %N%
goto done

:explain
if not exist "%JAR%" (
    echo [ERROR] 还没编译，先跑菜单 2。
    goto done
)
call :pickfile
if not defined FILE goto done
java %JAVAOPT% -jar "%JAR%" --explain "%FILE%"
goto done

:analyze
if not exist "%JAR%" (
    echo [ERROR] 还没编译，先跑菜单 2。
    goto done
)
call :pickfile
if not defined FILE goto done
java %JAVAOPT% -jar "%JAR%" --analyze "%FILE%"
goto done

:shell
"%MYSQL%" -u day37 -pday37 --default-character-set=utf8mb4 day37
goto done

:showindex
"%MYSQL%" -u day37 -pday37 --default-character-set=utf8mb4 day37 -e "SHOW INDEX FROM member; SELECT INDEX_LENGTH AS 索引总字节 FROM information_schema.TABLES WHERE TABLE_SCHEMA='day37' AND TABLE_NAME='member';"
goto done

:failed
echo.
echo [FAILED] 上一条命令失败了，往上翻看报错原文。
goto done

:quit
popd
exit /b

:done
echo.
if not "%~1"=="" goto quit
pause
set "PICK="
goto menu