@echo off
chcp 65001 >nul
title FastFood MIS Frontend (React Vite)

cd /d "%~dp0frontend"

echo ===========================================================================
echo   FASTFOOD MIS - REACT VITE FRONTEND
echo   URL: http://localhost:5173
echo ===========================================================================
echo.
echo Dang khoi dong React Frontend dev server...
echo.

call npm run dev
pause
