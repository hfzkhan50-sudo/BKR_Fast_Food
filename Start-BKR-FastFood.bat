@echo off
title BKR Fast Food
cd /d "%~dp0"

:: Use embedded portable JRE if available
if exist "%~dp0jre\bin\javaw.exe" (
    start "" "%~dp0jre\bin\javaw.exe" -jar "%~dp0BKR-FastFood.jar"
    exit
)
if exist "%~dp0jre\bin\java.exe" (
    start "" "%~dp0jre\bin\java.exe" -jar "%~dp0BKR-FastFood.jar"
    exit
)

:: Fallback: check system Java
where javaw >nul 2>nul
if %errorlevel% equ 0 (
    start "" javaw -jar "%~dp0BKR-FastFood.jar"
    exit
)

where java >nul 2>nul
if %errorlevel% equ 0 (
    start "" java -jar "%~dp0BKR-FastFood.jar"
    exit
)

:: Check common Java install paths
for /d %%i in ("C:\Program Files\Java\*") do (
    if exist "%%i\bin\javaw.exe" (
        start "" "%%i\bin\javaw.exe" -jar "%~dp0BKR-FastFood.jar"
        exit
    )
)

for /d %%i in ("C:\Program Files\OpenLogic\*") do (
    if exist "%%i\bin\javaw.exe" (
        start "" "%%i\bin\javaw.exe" -jar "%~dp0BKR-FastFood.jar"
        exit
    )
)

for /d %%i in ("C:\Program Files\Eclipse Adoptium\*") do (
    if exist "%%i\bin\javaw.exe" (
        start "" "%%i\bin\javaw.exe" -jar "%~dp0BKR-FastFood.jar"
        exit
    )
)

echo ============================================================
echo  Java runtime not found!
echo  Please make sure the "jre" folder is in the same directory.
echo ============================================================
pause
