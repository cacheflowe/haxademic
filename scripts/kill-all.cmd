taskkill /F /T /IM node.exe
wmic process where "name='node.exe'" delete
taskkill /F /T /IM cmd.exe
wmic process where "name='cmd.exe'" delete
taskkill /F /T /IM chrome.exe
wmic process where "name='chrome.exe'" delete
taskkill /F /T /IM java.exe
wmic process where "name='java.exe'" delete
taskkill /F /T /IM javaw.exe
wmic process where "name='javaw.exe'" delete
exit
