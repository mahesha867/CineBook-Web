@echo off
title CineBook Web - Local Launcher
cd /d "%~dp0backend"

echo ===================================================
echo           CineBook Web - Local Launcher
echo ===================================================
echo.
echo Choose Database to connect to:
echo  [1] Cloud Database (Aiven / Custom)
echo  [2] Local MySQL (localhost:3306)
echo.
set /p choice="Enter choice (1 or 2, default is 1): "

if "%choice%"=="2" (
    echo.
    echo Starting CineBook Web with Local MySQL...
    set "DB_URL=jdbc:mysql://localhost:3306/cinebook_web?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    set "DB_USER=root"
    set "DB_PASSWORD=root"
) else (
    echo.
    echo Starting CineBook Web with Cloud Database...
    set /p DB_URL="Enter DB_URL: "
    set /p DB_USER="Enter DB_USER: "
    set /p DB_PASSWORD="Enter DB_PASSWORD: "
)

echo.
echo Launching Spring Boot on http://localhost:8080 ...
call mvnw.cmd spring-boot:run
pause

