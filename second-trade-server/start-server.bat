@echo off
chcp 65001
echo [INFO] Checking environment...

REM Check Java environment
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java environment not found. Please install JDK
    pause
    exit /b 1
)

REM Check Maven environment
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven not found in PATH
    pause
    exit /b 1
)

echo [INFO] Environment check passed, starting service...

REM Change to project directory
cd /d %~dp0

REM Check if pom.xml exists
if not exist "pom.xml" (
    echo [ERROR] pom.xml not found
    pause
    exit /b 1
)

echo [INFO] Compiling project...
call mvn clean install -Dfile.encoding=UTF-8
if %errorlevel% neq 0 (
    echo [ERROR] Project compilation failed
    pause
    exit /b 1
)

echo [INFO] Starting service...
call mvn spring-boot:run -Dfile.encoding=UTF-8 -Dspring.profiles.active=dev

pause 