@echo off
title GardenConnect - Shutdown Services

echo ========================================================
echo        Stopping GardenConnect Services...              
echo ========================================================
echo.

set "TOMCAT_DIR=C:\Users\ignit\apache-tomcat-10.1.34"
set "MYSQL_DIR=C:\Program Files\MySQL\MySQL Server 8.0\bin"

if defined CATALINA_HOME (
    set "TOMCAT_DIR=%CATALINA_HOME%"
)

:: 1. Stop Apache Tomcat
if exist "%TOMCAT_DIR%\bin\shutdown.bat" (
    echo [INFO] Stopping Apache Tomcat 10.1...
    set "CATALINA_HOME=%TOMCAT_DIR%"
    set "CATALINA_BASE=%TOMCAT_DIR%"
    call "%TOMCAT_DIR%\bin\shutdown.bat" >nul 2>&1
    echo [OK] Tomcat stop command issued.
)

:: 2. Stop standalone MySQL
echo [INFO] Stopping MySQL Server...
if exist "%MYSQL_DIR%\mysqladmin.exe" (
    "%MYSQL_DIR%\mysqladmin.exe" -u root shutdown >nul 2>&1
)
taskkill /F /IM mysqld.exe >nul 2>&1
echo [OK] MySQL stopped.

echo.
echo ========================================================
echo        All GardenConnect services stopped.             
echo ========================================================
timeout /t 3 >nul
exit /b 0
