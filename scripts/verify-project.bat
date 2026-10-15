@echo off
REM ========================================================
REM GardenConnect - Project Verification Script (Windows CMD)
REM ========================================================

echo ============================================
echo   GardenConnect Build ^& Verification Script
echo ============================================

REM Check Java
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH.
    pause
    exit /b 1
)
echo [OK] Java detected.

REM Locate Maven
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set MVN_CMD=mvn
) else if exist "%USERPROFILE%\apache-maven-3.9.9\bin\mvn.cmd" (
    set MVN_CMD="%USERPROFILE%\apache-maven-3.9.9\bin\mvn.cmd"
) else if exist "mvnw.cmd" (
    set MVN_CMD=mvnw.cmd
) else (
    echo [ERROR] Maven not found.
    pause
    exit /b 1
)

echo [INFO] Running test suite...
call %MVN_CMD% clean test
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Tests failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [INFO] Building WAR artifact...
call %MVN_CMD% package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven packaging failed!
    pause
    exit /b %ERRORLEVEL%
)

if exist "target\gardenconnect.war" (
    echo.
    echo [SUCCESS] GardenConnect WAR package created successfully at target\gardenconnect.war
    echo Deploy this WAR into Tomcat 10.1 webapps\ directory.
) else (
    echo [ERROR] target\gardenconnect.war not found.
)

pause
