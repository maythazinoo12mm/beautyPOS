@echo off
cd /d %~dp0

echo ============================================
echo  beautyPOS - Running automated tests...
echo  (PostgreSQL not required, uses H2 automatically)
echo ============================================
echo.

call "%~dp0gradlew.bat" test

echo.
echo ============================================
echo  Opening the test report in your browser...
echo  (shows all cases, passed and failed)
echo ============================================
start "" "%~dp0build\reports\tests\test\index.html"

echo.
echo You can close this window now.
pause >nul
