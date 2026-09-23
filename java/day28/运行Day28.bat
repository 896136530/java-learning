@echo off
chcp 65001>nul
setlocal enabledelayedexpansion
set MYSQL=E:\mysql\bin\mysql.exe
set MYSQLD=E:\mysql\bin\mysqld.exe
set MYSQL_PWD=root
set JAR=%~dp0..\day21\lib\mysql-connector-j.jar
set SRC=%~1
if not "%SRC%"=="" goto check

echo.
echo ============ Day28 JAVA FILE LIST ============
set N=0
for %%f in (Day28_*.java) do (
    set /a N+=1
    set "F!N!=%%f"
    echo    !N!. %%f
)
echo ==============================================
if %N%==0 (echo [NO Day28_*.java HERE] & pause & exit /b)
set PICK=
set /p PICK=Select number then press Enter (default 1): 
if "%PICK%"=="" set PICK=1
call set "SRC=%%F%PICK%%%"
if "%SRC%"=="" (echo [INVALID NUMBER] & pause & exit /b)

:check
rem ---- 1) MySQL 没在跑就先拉起来 ----
"%MYSQL%" -u root -e "select 1" >nul 2>&1
if not errorlevel 1 goto compile
echo [note] MySQL is not running - starting it with mysqld ...
start "" "%MYSQLD%" --defaults-file=E:\mysql\my.ini
timeout /t 8 >nul
"%MYSQL%" -u root -e "select 1" >nul 2>&1
if errorlevel 1 (echo [ERROR] MySQL start failed & pause & exit /b)

:compile
for %%f in ("%SRC%") do set "MAIN=%%~nf"
rem ---- day28 的三个公共零件：Ex*/Review 都要用，一起编 ----
echo.
echo Compiling %SRC% ...
set "COMMON="
for %%c in (Db.java Setup.java) do if exist "%~dp0%%c" set "COMMON=!COMMON! "%~dp0%%c""
javac -encoding UTF-8 -cp "%JAR%" "%~dp0%SRC%" %COMMON% 2>&1
if errorlevel 1 (echo COMPILE FAILED & pause & exit /b)

echo.
echo ============ RUN OUTPUT ============
java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "%~dp0;%JAR%" %MAIN%
echo ==================================
pause