@echo off
chcp 65001 >nul
title FastFood MIS Backend (Spring Boot 3)

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "MAVEN_BIN=%~dp0..\..\.tools\apache-maven-3.9.6\bin"
set "PATH=%JAVA_HOME%\bin;%MAVEN_BIN%;%PATH%"

cd /d "%~dp0"

echo ===========================================================================
echo   FASTFOOD MIS - SPRING BOOT 3 BACKEND
echo   Java: JDK 17 (C:\Program Files\Java\jdk-17)
echo   MySQL: localhost:3306/fastfood_db
echo ===========================================================================
echo.
echo Dang khoi dong Spring Boot Backend tai http://localhost:8080 ...
echo.

call "%MAVEN_BIN%\mvn.cmd" spring-boot:run -DskipTests
pause
