@echo off
chcp 65001>nul
title Day35 建库（第 1 步）
setlocal
rem ================================================================
rem  Day35 建库一键脚本：执行同目录的 建库.sql
rem  ⚠️ MySQL 路径按本机写的（装在 E:\mysql）；换机器请改这两行
rem ================================================================
set MYSQL=E:\mysql\bin\mysql.exe
set MYSQLD=E:\mysql\bin\mysqld.exe
set MYINI=E:\mysql\my.ini
set HERE=%~dp0
set MYSQL_PWD=root

if not exist "%MYSQL%" (
    echo [ERROR] 找不到 %MYSQL%
    echo         请把本脚本里的 MYSQL 改成你机器上 mysql.exe 的路径
    pause & exit /b
)

rem ---- 1) MySQL 没在跑就先拉起来（服务要管理员权限，这里直接起进程）----
"%MYSQL%" -u root -e "select 1" >nul 2>&1
if not errorlevel 1 goto up
echo [1/2] MySQL 没在跑，正在启动 mysqld ...
start "" "%MYSQLD%" --defaults-file=%MYINI%
for /l %%i in (1,1,10) do (
    timeout /t 2 >nul
    "%MYSQL%" -u root -e "select 1" >nul 2>&1
    if not errorlevel 1 goto up
)
echo [ERROR] 20 秒还没起来，看错误日志：E:\mysql\data\*.err
pause & exit /b

:up
echo [2/2] MySQL 正常，开始执行 建库.sql ...
echo.
"%MYSQL%" -u root --default-character-set=utf8mb4 < "%HERE%建库.sql"
echo.
echo ================================================
echo  [DONE] 上面就是执行结果。
echo  把这三段抄进交作业表（第 1 步）：
echo     SELECT VERSION()          -^> 版本那一行
echo     SHOW CREATE DATABASE      -^> 要有 utf8mb4 + utf8mb4_0900_ai_ci
echo     SHOW GRANTS FOR 'day35'   -^> 要有 ON `day35`.*
echo ================================================
pause
