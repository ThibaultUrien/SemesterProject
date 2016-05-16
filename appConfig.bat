@echo off

:: BatchGotAdmin 
:: I need admin to edit java cacert
:-------------------------------------
REM  --> Check for permissions
    IF "%PROCESSOR_ARCHITECTURE%" EQU "amd64" (
>nul 2>&1 "%SYSTEMROOT%\SysWOW64\cacls.exe" "%SYSTEMROOT%\SysWOW64\config\system"
) ELSE (
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
)

REM --> If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
    echo Requesting administrative privileges...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    set params = %*:"=""
    echo UAC.ShellExecute "cmd.exe", "/c ""%~s0"" %params%", "", "runas", 1 >> "%temp%\getadmin.vbs"

    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    pushd "%CD%"
    CD /D "%~dp0"
:-------------------------------------- 

for /f %%j in ("java.exe") do set javapath=%%~dp$PATH:j
  
echo %javapath%
dir %javapath% | find "<SYMLINK>" > temp.txt
for /f "tokens=5" %%i in (temp.txt) do set a=%%i
for /f "tokens=6" %%i in (temp.txt) do set b=%%i
set pathToJava=%a% %b%
set pathToJava=%pathToJava:~1,-1%
echo %pathToJava%
for %%a in ("%pathToJava%") do (
    set filepath=%%~dpa
)  

set mypathe=%cd%
echo %mypathe%
cd %filepath%
cd ..
set jrepath=%cd%
"%jrepath%\bin\keytool" -import -keystore "%jrepath%\lib\security\cacerts" -file %mypathe%\StartComClass1PrimaryIntermediateServerCA.crt
cd %mypathe%
::@setlocal
::	for %%a in (%javapath%) do (
::	echo %d%
::	set c=%d%	
::	set d=%%a) 
::echo %c% %d%
::cd %d%
::pause 
::cd %mypathe%
::for /f %%a in (%javapath%) do set d=%%a
::echo.path   : %javapath%
::echo.d      : %d%
::echo.
::keytool -import -keystore cacerts -file StartComClass1PrimaryIntermediateServerCA.crt