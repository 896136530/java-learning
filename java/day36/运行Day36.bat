@echo off
chcp 65001>nul
setlocal enabledelayedexpansion
rem ================================================================
rem  Day36 一键脚本：MySQL 索引（一）
rem  1=建库造数  2=编译  3=计时  4=EXPLAIN  5=进 mysql  6=看索引
rem ================================================================
set MVN=C:\Users\89613\bin\mvn.bat
if not exist "%MVN%" set MVN=C:\Users\89613\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd
set HERE=%~dp0
set PICK=%~1
rem ⚠️ 按本机写的；换机器改成你自己 mysql.exe 的路径
set MYSQL=E:\mysql\bin\mysql.exe
set JAR=%HERE%target\day36-index.jar
set JAVAOPT=-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8

if not exist "%MYSQL%" (
    echo [ERROR] 找不到 mysql.exe：%MYSQL%
    echo         改这个 .bat 里的 MYSQL 变量即可。
    pause & exit /b
)

:menu
if not "%PICK%"=="" goto run
echo.
echo ============ Day36  MySQL 索引（一） ============
echo   1. 建库 + 造 20 万行（会 DROP 重建 member 表）
echo   2. 编译打包（mvn package）
echo   3. 计时（输入 .sql 文件，默认 sql\90_我的实验.sql）
echo   4. EXPLAIN（输入 .sql 文件，默认 sql\90_我的实验.sql）
echo   5. 进 mysql 命令行（day36 / day36）
echo   6. 看 member 上现有的索引
echo   0. 退出
echo =================================================
set "PICK="
set /p PICK=请选择：
echo.

:run
if "%PICK%"=="1" goto setup
if "%PICK%"=="2" goto build
if "%PICK%"=="3" goto bench
if "%PICK%"=="4" goto explain
if "%PICK%"=="5" goto shell
if "%PICK%"=="6" goto showindex
if "%PICK%"=="0" exit /b
echo [ERROR] 没有这个选项：%PICK%
goto menu

:setup
echo [1/3] 建库 day36 + 账号 day36 + 造 20 万行（约 10~20 秒）...
"%MYSQL%" -u root -proot --default-character-set=utf8mb4 < "%HERE%sql\01_setup.sql"
if errorlevel 1 goto failed
echo.
echo 完成。继续请选 2 编译。
goto done

:build
if not exist "%MVN%" (
    echo [ERROR] 找不到 Maven：%MVN%
    pause & exit /b
)
echo 编译打包中（第一次可能要几十秒）...
call "%MVN%" -q package -DskipTests -f "%HERE%pom.xml"
if errorlevel 1 goto failed
echo.
echo 完成，产物：target\day36-index.jar
goto done

:bench
set "F=sql\90_我的实验.sql"
set /p F=sql 文件（回车用 sql\90_我的实验.sql）：
if not exist "%F%" set "F=%HERE%%F%"
if not exist "%F%" (
    echo [ERROR] 找不到文件：%F%
    goto done
)
set "N=20"
set /p N=每条跑几次（回车 20）：
java %JAVAOPT% -jar "%JAR%" "%F%" %N%
goto done

:explain
set "F=sql\90_我的实验.sql"
set /p F=sql 文件（回车用 sql\90_我的实验.sql）：
if not exist "%F%" set "F=%HERE%%F%"
if not exist "%F%" (
    echo [ERROR] 找不到文件：%F%
    goto done
)
java %JAVAOPT% -jar "%JAR%" --explain "%F%"
goto done

:shell
"%MYSQL%" -u day36 -pday36 --default-character-set=utf8mb4 day36
goto done

:showindex
"%MYSQL%" -u day36 -pday36 --default-character-set=utf8mb4 day36 -e "SHOW INDEX FROM member"
goto done

:failed
echo.
echo [FAILED] 上一条命令失败了，往上翻看报错原文。
goto done

:done
echo.
if not "%~1"=="" exit /b
pause
set "PICK="
goto menu
