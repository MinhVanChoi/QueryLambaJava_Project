@echo off
chcp 65001 >nul
title FastFood MIS - Khoi Dong Full-Stack

echo ===========================================================================
echo   KHOI DONG HE THONG FASTFOOD MIS FULL-STACK
echo   1. Backend Spring Boot (Port 8080)
echo   2. Frontend React Vite (Port 5173)
echo ===========================================================================
echo.

start "FastFood Backend (Spring Boot)" cmd /k ""%~dp0run_backend.bat""
timeout /t 5 >nul
start "FastFood Frontend (React)" cmd /k ""%~dp0run_frontend.bat""

echo.
echo He thong dang khoi dong trong 2 cua so rieng biet.
echo Ban hay mo trinh duyet tai: http://localhost:5173
echo.
pause
