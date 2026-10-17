@echo off
chcp 65001 > nul
echo =====================================================================
echo   DANG KHOI CHAY FAST FOOD MANAGEMENT SYSTEM (MIS) - NHOM 17
echo =====================================================================
set "JAVA_EXE=C:\Program Files\Java\jdk-17\bin\java.exe"
set "CP=target/classes;C:\Users\Asus\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar;C:\Users\Asus\.m2\repository\com\mysql\mysql-connector-j\9.7.0\mysql-connector-j-9.7.0.jar"

"%JAVA_EXE%" -Dfile.encoding=UTF-8 -cp "%CP%" com.nhom17.Main
pause
