taskkill /f /t /im javaw.exe
taskkill /f /t /im java.exe
wmic process where "name='javaw.exe'" delete
wmic process where "name='java.exe'" delete
exit
