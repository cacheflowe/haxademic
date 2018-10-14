REM | Info from: https://superuser.com/questions/972038/how-to-get-rid-of-updates-are-available-message-in-windows-10/1006199#1006199

cd /d "%Windir%\System32"
icacls musnotification.exe /remove:d Everyone
icacls musnotification.exe /grant Everyone:F
icacls musnotification.exe /setowner "NT SERVICE\TrustedInstaller"
icacls musnotification.exe /remove:g Everyone
icacls musnotificationux.exe /remove:d Everyone
icacls musnotificationux.exe /grant Everyone:F
icacls musnotificationux.exe /setowner "NT SERVICE\TrustedInstaller"
icacls musnotificationux.exe /remove:g Everyone

pause