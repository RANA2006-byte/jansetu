@echo off
:: ============================================================
:: run.bat — Compile and run Jansetu backend (Windows)
::
:: Usage:
::   run.bat          -> Starts Jansetu HTTP API Server (port 8080)
::   run.bat server   -> Starts Jansetu HTTP API Server (port 8080)
::   run.bat db       -> Runs DBConnection test
::   run.bat main     -> Runs Main test
:: ============================================================

set JAR=lib\mysql-connector-j-26.10.0.jar
set SRC=src
set OUT=out

:: Create output folder if it doesn't exist
if not exist %OUT% mkdir %OUT%

echo Compiling all Java files...
javac -cp "%JAR%" -sourcepath "%SRC%" -d "%OUT%" ^
  "%SRC%\model\*.java" ^
  "%SRC%\auth\*.java" ^
  "%SRC%\dao\*.java" ^
  "%SRC%\service\*.java" ^
  "%SRC%\util\*.java" ^
  "%SRC%\server\*.java" ^
  "%SRC%\Main.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation FAILED. Fix the errors above first.
    pause
    exit /b 1
)

echo Compilation successful!
echo.

:: Decide what to run
if "%1"=="db" (
    echo Running DBConnection test...
    java -cp "%OUT%;%JAR%" util.DBConnection
) else if "%1"=="main" (
    echo Running Main...
    java -cp "%OUT%;%JAR%" Main
) else (
    echo Starting Jansetu API Server on http://localhost:8080...
    java -cp "%OUT%;%JAR%" server.JansetuServer
)

pause
