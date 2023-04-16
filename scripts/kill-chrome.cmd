taskkill /f /t /im chrome.exe
wmic process where "name='chrome.exe'" delete
exit
