@echo off
setlocal
cd /d "%~dp0"
if not exist build mkdir build
javac -encoding UTF-8 -d build src\SnakeGame.java
if errorlevel 1 (
  pause
  exit /b 1
)
java -cp build SnakeGame
