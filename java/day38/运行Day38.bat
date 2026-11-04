@echo off
chcp 65001>nul
setlocal enabledelayedexpansion
rem ================================================================
rem  Day38 一键脚本：MyBatis 基础
rem  1=建库造数  2=编译  3=运行  4=运行(DEBUG，看真实 SQL)  5=mysql
rem ================================================================
rem 切到脚本目录，后面全用相对路径（路径里有中文和括号，绝对路径容易踩 cmd 的坑）
pushd "%~dp0"
set MVN=C:\Users\89613\bin\mvn.bat
if not exist "%MVN%" set MVN=C:\Users\89613\.vscode\extensions\oracle.oracle-java-26.0.1\nbcode\java\maven\bin\mvn.cmd
set MYSQL=E:\mysql\bin\mysql.exe
set JAR=target\day38-mybatis.jar
set PICK=%~1

if not exist "%MYSQL%" (
    echo [ERROR] 找不到 mysql.exe：%MYSQL%
    pause & exit /b
)

:menu
if not "%PICK%"=="" goto run
echo.
echo ============ Day38  MyBatis 基础 ============
echo   1. 建库 + 造数据（day38：8 个学生 + 3 个班）
echo   2. 编译打包（mvn package）
echo   3. 运行演示（看 5 个已写好的例子 + 5 个 TODO）
echo   4. 运行演示（DEBUG：打出 MyBatis 真正发的 SQL）
echo   5. 进 mysql 命令行（day38 / day38）
echo   0. 退出
echo ============================================
set "PICK="
set /p PICK=请选择：
echo.

:run
if "%PICK%"=="1" goto setup
if "%PICK%"=="2" goto build
if "%PICK%"=="3" goto play
if "%PICK%"=="4" goto playdebug
if "%PICK%"=="5" goto shell
if "%PICK%"=="0" goto quit
echo [ERROR] 没有这个选项：%PICK%
goto menu

:setup
echo [1/2] 建库 day38 + 账号 + 8 个学生 / 3 个班 ...
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
echo 完成，产物：target\day38-mybatis.jar
goto done

:play
if not exist "%JAR%" (
    echo [ERROR] 还没编译，先跑菜单 2。
    goto done
)
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -jar "%JAR%"
goto done

:playdebug
if not exist "%JAR%" (
    echo [ERROR] 还没编译，先跑菜单 2。
    goto done
)
echo （下面 DEBUG 行就是 MyBatis 真正发出的 SQL 和参数）
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -jar "%JAR%"
goto done

:shell
"%MYSQL%" -u day38 -pday38 --default-character-set=utf8mb4 day38
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