taskkill /f /t /im node.exe
wmic process where "name='node.exe'" delete
exit
