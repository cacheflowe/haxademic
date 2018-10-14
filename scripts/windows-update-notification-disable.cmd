REM | Info from: https://superuser.com/questions/972038/how-to-get-rid-of-updates-are-available-message-in-windows-10/1006199#1006199

cd /d "%Windir%\System32"
takeown /f musnotification.exe
icacls musnotification.exe /deny Everyone:(X)
takeown /f musnotificationux.exe
icacls musnotificationux.exe /deny Everyone:(X)

pause