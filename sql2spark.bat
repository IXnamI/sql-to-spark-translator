@echo off
set SCRIPT_DIR=%~dp0

java -cp "%SCRIPT_DIR%target\sql-to-spark-1.0-SNAPSHOT.jar" com.github.xnam.Main %*