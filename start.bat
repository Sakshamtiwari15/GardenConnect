@echo off
setlocal enabledelayedexpansion
title GardenConnect - One-Click Launcher

echo ========================================================
echo        GardenConnect - One-Click Local Launcher        
echo ========================================================
echo.

set "PROJECT_DIR=%~dp0"
set "TOMCAT_DIR=C:\Users\ignit\apache-tomcat-10.1.34"
set "MYSQL_DIR=C:\Program Files\MySQL\MySQL Server 8.0\bin"
set "DATA_DIR=%PROJECT_DIR%database\data"

:: Check if custom CATALINA_HOME is defined
if defined CATALINA_HOME (
    set "TOMCAT_DIR=%CATALINA_HOME%"
)

:: 1. Verify MySQL Directory
if not exist "%MYSQL_DIR%\mysqld.exe" (
    echo [ERROR] MySQL not found at: %MYSQL_DIR%
    echo Please edit start.bat and set MYSQL_DIR to your MySQL bin path.
    pause
    exit /b 1
)

:: 2. Verify Tomcat Directory
if not exist "%TOMCAT_DIR%\bin\catalina.bat" (
    echo [ERROR] Tomcat 10.1 not found at: %TOMCAT_DIR%
    echo Please edit start.bat and set TOMCAT_DIR to your Tomcat root.
    pause
    exit /b 1
)

:: 3. Initialize MySQL Data Directory if not present
if not exist "%DATA_DIR%" (
    echo [INFO] First-time setup: Initializing local MySQL database directory...
    "%MYSQL_DIR%\mysqld.exe" --initialize-insecure --datadir="%DATA_DIR%"
    if errorlevel 1 (
        echo [ERROR] Failed to initialize MySQL data directory.
        pause
        exit /b 1
    )
    echo [OK] MySQL data directory initialized.
)

:: 4. Check if MySQL is running on port 3306
netstat -ano | findstr /R ":3306 .*LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [INFO] Starting MySQL Server on port 3306...
    start "GardenConnect-MySQL" /min "%MYSQL_DIR%\mysqld.exe" --datadir="%DATA_DIR%" --port=3306 --console
    timeout /t 3 /nobreak >nul
) else (
    echo [OK] MySQL Server is already running on port 3306.
)

:: 5. Ensure GardenConnect database and seed data are imported
"%MYSQL_DIR%\mysql.exe" -u root -e "USE gardenconnect; SELECT 1 FROM users LIMIT 1;" >nul 2>&1
if errorlevel 1 (
    echo [INFO] Setting up 'gardenconnect' database schema and seed data...
    "%MYSQL_DIR%\mysql.exe" -u root -e "CREATE DATABASE IF NOT EXISTS gardenconnect CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    "%MYSQL_DIR%\mysql.exe" -u root gardenconnect < "%PROJECT_DIR%database\schema.sql"
    "%MYSQL_DIR%\mysql.exe" -u root gardenconnect < "%PROJECT_DIR%database\seed.sql"
    echo [OK] Database schema and demo seeds imported successfully.
) else (
    echo [OK] Database 'gardenconnect' is ready.
)

:: 6. Ensure target/gardenconnect.war exists
if not exist "%PROJECT_DIR%target\gardenconnect.war" (
    echo [INFO] Packaging gardenconnect.war with Maven...
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
)

:: 7. Deploy WAR to Tomcat webapps
echo [INFO] Deploying gardenconnect.war to Tomcat...
copy /Y "%PROJECT_DIR%target\gardenconnect.war" "%TOMCAT_DIR%\webapps\" >nul
echo [OK] Deployment updated.

:: 8. Set Tomcat Environment Variables
set "CATALINA_HOME=%TOMCAT_DIR%"
set "CATALINA_BASE=%TOMCAT_DIR%"

:: 9. Check if Tomcat is running on port 8085 (or 8080)
netstat -ano | findstr /R ":8085 .*LISTENING" >nul 2>&1
if errorlevel 1 (
    netstat -ano | findstr /R ":8080 .*LISTENING" >nul 2>&1
    if errorlevel 1 (
        echo [INFO] Starting Apache Tomcat 10.1...
        start "GardenConnect-Tomcat" "%TOMCAT_DIR%\bin\startup.bat"
        echo [INFO] Waiting for Tomcat to initialize...
        timeout /t 5 /nobreak >nul
    ) else (
        echo [OK] Tomcat is already running on port 8080.
    )
) else (
    echo [OK] Tomcat is already running on port 8085.
)

echo.
echo ========================================================
echo        GardenConnect is RUNNING!                       
echo ========================================================
echo.
echo   Public URL:    http://localhost:8085/gardenconnect/
echo   Login Portal:  http://localhost:8085/gardenconnect/login
echo.
echo   Demo Credentials:
echo   - Administrator: admin / Admin@123456
echo   - Gardener:      sarah_green / Gardener@123
echo.
echo ========================================================
echo.

:: Open default browser
start http://localhost:8085/gardenconnect/

echo Press any key to close this launcher window (services keep running in background)...
pause >nul
exit /b 0
