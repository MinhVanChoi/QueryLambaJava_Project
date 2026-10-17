@echo off
chcp 65001 > nul
echo ===========================================================================
echo   FAST FOOD MIS - WEBSITE BAN THUC AN NHANH (LINEAR / NOTION EDITION)
echo   Dang khoi dong may chu Web va Co so du lieu...
echo ===========================================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
set "JAVAC_EXE=%JAVA_HOME%\bin\javac.exe"
set "CP=target/classes;C:\Users\Asus\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar;C:\Users\Asus\.m2\repository\com\mysql\mysql-connector-j\9.7.0\mysql-connector-j-9.7.0.jar"

"%JAVAC_EXE%" -encoding UTF-8 -d target/classes -cp "%CP%" src/main/java/com/nhom17/web/JsonUtil.java src/main/java/com/nhom17/web/WebServer.java

start "" http://localhost:8080
"%JAVA_EXE%" -Dfile.encoding=UTF-8 -cp "%CP%" com.nhom17.web.WebServer 8080
pause
