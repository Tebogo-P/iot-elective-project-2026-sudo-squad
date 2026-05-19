@echo off
REM Quick Start Script for Windows
REM Frontend-Backend Integration

echo =========================================
echo Sudo Squad Attendance System - Quick Start
echo =========================================
echo.

REM Check prerequisites
echo [1/5] Checking Prerequisites...
java -version 2>&1 | findstr /C:"version" >nul 2>&1
if %errorlevel% equ 0 (
    echo   - Java: OK
) else (
    echo   - Java: NOT FOUND
)

mvn -v >nul 2>&1
if %errorlevel% equ 0 (
    echo   - Maven: OK
) else (
    echo   - Maven: NOT FOUND
)

mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo   - MySQL: OK
) else (
    echo   - MySQL: May not be in PATH
)

echo.
echo [2/5] Building Backend...
call mvn clean install -DskipTests -q
if %errorlevel% equ 0 (
    echo OK - Backend built successfully
) else (
    echo ERROR - Build failed. Check pom.xml and dependencies
    pause
    exit /b 1
)

echo.
echo [3/5] Starting Backend...
echo   Backend will run on http://localhost:8080/api/v1
echo   (Running in background)
echo.
echo [4/5] Verifying Backend Health...
timeout /t 5 /nobreak
curl -s http://localhost:8080/api/v1/attendance/health

echo.
echo [5/5] Starting Frontend...
echo   Frontend available at http://localhost:3000
echo.
echo   Verify:
echo   - Dashboard displays without errors
echo   - "ESP32 Connection" shows ONLINE
echo   - Table is populated
echo.

REM Try to start frontend server
if exist "node_modules\.bin\http-server.cmd" (
    call npx http-server -p 3000 -c-1
) else (
    echo Opening index.html in browser...
    echo This is a manual open - file:///c:/Users/HP/Documents/iot-elective-project-2026-sudo-squad/index.html
    pause
)
