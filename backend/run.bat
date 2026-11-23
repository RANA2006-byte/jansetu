@echo off
:: ============================================================
:: run.bat — Compile and run Jansetu backend (Windows)
::
:: Usage:
::   Double-click this file, OR
::   Open Command Prompt in backend\ and type: run.bat
::
:: To test DBConnection only:
::   run.bat db
:: ============================================================

set JAR=lib\mysql-connector-j-26.10.0.jar
set SRC=src
set OUT=out

:: Create output folder if it doesn't exist
if not exist %OUT% mkdir %OUT%

echo Compiling all Java files...
javac -cp "%JAR%" -sourcepath "%SRC%" -d "%OUT%" ^
  "%SRC%\model\User.java" ^
  "%SRC%\model\Scheme.java" ^
  "%SRC%\util\DBConnection.java" ^
  "%SRC%\dao\UserDAO.java" ^
  "%SRC%\dao\SchemeDAO.java" ^
  "%SRC%\service\RecommendationService.java" ^
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
) else (
    echo Running Main...
    java -cp "%OUT%;%JAR%" Main
)

pause
